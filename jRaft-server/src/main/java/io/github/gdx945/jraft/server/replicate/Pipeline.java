package io.github.gdx945.jraft.server.replicate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.thread.ThreadUtil;
import io.github.gdx945.jraft.server.Node;
import io.github.gdx945.jraft.server.model.LogEntry;
import io.github.gdx945.jraft.server.replicate.model.IdxAndFuture;
import io.github.gdx945.jraft.server.rpc.method.ServerRpcMethod;
import io.github.gdx945.jraft.server.rpc.param.AppendEntriesReq;
import io.github.gdx945.jraft.server.rpc.param.AppendEntriesResp;
import io.github.gdx945.jraft.server.store.LogEntriesStore;
import io.github.gdx945.jraft.server.store.NodeStateStore;
import io.github.gdx945.rpc.InvokeFuture;
import io.github.gdx945.rpc.RpcClient;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-16 10:08:01
 * @since : 0.1
 */
public class Pipeline {

    private final static Logger logger = LoggerFactory.getLogger(Pipeline.class);

    public Pipeline(String nodeId, RpcClient rpcClient, int replicationTimeout) {
        this.nodeId = nodeId;
        this.rpcClient = rpcClient;
        this.replicationTimeout = replicationTimeout;

        this.leaderCommit = Node.getNodeById(this.nodeId).getLeaderCommit();
        this.logEntriesStore = Node.getNodeById(this.nodeId).getLogEntriesStore();
        this.nodeStateStore = Node.getNodeById(this.nodeId).getNodeStateStore();

        new Thread(this::doReplicate, "pipeline-" + rpcClient.getAddr()).start();
    }

    public String getAddr() {
        return this.rpcClient.getAddr();
    }

    private String nodeId;

    private RpcClient rpcClient;

    private Supplier<Integer> leaderCommit;

    private int replicationTimeout;

    private LogEntriesStore logEntriesStore;

    private NodeStateStore nodeStateStore;

    //    private final Queue<IdxAndFuture> queue = new PriorityBlockingQueue<>();

    private final Queue<IdxAndFuture> queue = new LinkedList<>();

    private final int maxSizeOnce = 32;

    private int nextLogIndex = 1;

    private volatile boolean start = false;

    private ExecutorService awaitReplicateExecutorService = Executors.newFixedThreadPool(8, new DefaultThreadFactory("Pipeline_AwaitReplicate"));

    private Lock doReplicateLock = new ReentrantLock();

    private Condition doReplicateLockCondition = doReplicateLock.newCondition();

    public void start() {
        this.start = true;
        if (this.logEntriesStore.getLastLogEntryIndex() > 0) {
            this.nextLogIndex = this.logEntriesStore.getLastLogEntryIndex();
        }
        signalDoReplicate();
    }

    public void stop() {
        this.start = false;
    }

    // todo 外部调用的时候保证顺序
    public void replicate(IdxAndFuture idxAndFuture) {
        this.queue.add(idxAndFuture);
        signalDoReplicate();
    }

    private void doReplicate() {
        int _count = 0;

        for (; ; ) {
            long timeMillis = System.currentTimeMillis();
            if (this.start && this.nextLogIndex <= this.logEntriesStore.getLastLogEntryIndex()) {
                _count = 0;

                AppendEntriesReq appendEntriesReq = new AppendEntriesReq();
                appendEntriesReq.setLeaderId(this.nodeId);
                appendEntriesReq.setTerm(this.nodeStateStore.getCurrentTerm());
                appendEntriesReq.setLeaderCommit(this.leaderCommit.get());

                List<LogEntry> logEntries;
                if (this.nextLogIndex == 1) {
                    logEntries = this.logEntriesStore.getList(1, this.maxSizeOnce);
                    appendEntriesReq.setEntries(logEntries);
                    appendEntriesReq.setPrevLogIndex(0);
                }
                else {
                    logEntries = this.logEntriesStore.getList(this.nextLogIndex - 1, this.maxSizeOnce + 1);
                    LogEntry prevLog = logEntries.get(0);
                    appendEntriesReq.setEntries(new ArrayList<>(logEntries.subList(1, logEntries.size())));
                    appendEntriesReq.setPrevLogIndex(prevLog.getIndex());
                    appendEntriesReq.setPrevLogTerm(prevLog.getTerm());
                }

                try {
                    InvokeFuture invokeFuture = rpcClient.invokeAsyn(ServerRpcMethod.APPEND_ENTRIES, appendEntriesReq, this.replicationTimeout);
                    this.nextLogIndex = logEntries.get(logEntries.size() - 1).getIndex() + 1;
                    awaitReplicateExecutorService
                        .submit(new ReplicateDone(invokeFuture, this.replicationTimeout - (System.currentTimeMillis() - timeMillis)));
                }
                catch (TimeoutException e) {
                    logger.error("pipeline replicate invoke failed", e);
                    // ignore
                }
            }
            else {
                if (_count++ < this.maxSizeOnce) {
                    ThreadUtil.sleep(1);
                    continue;
                }

                waitDoReplicate();
            }
        }
    }

    private void waitDoReplicate() {
        this.doReplicateLock.lock();
        try {
            this.doReplicateLockCondition.await();
        }
        catch (InterruptedException e) {
            // ignore
        }
        this.doReplicateLock.unlock();
    }

    private void signalDoReplicate() {
        this.doReplicateLock.lock();
        this.doReplicateLockCondition.signal();
        this.doReplicateLock.unlock();
    }

    private class ReplicateDone implements Runnable {

        ReplicateDone(InvokeFuture<AppendEntriesResp> invokeFuture, long timeout) {
            this.invokeFuture = invokeFuture;
            this.timeout = timeout;
        }

        private InvokeFuture<AppendEntriesResp> invokeFuture;

        private long timeout;

        @Override
        public void run() {
            AppendEntriesResp appendEntriesResp;
            try {
                appendEntriesResp = this.invokeFuture.get(this.timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException | TimeoutException | ExecutionException e) {
                logger.error("pipeline replicate get future failed", e);
                start();
                return;
            }

            int _nextLogIndex = appendEntriesResp.getNextLogIndex();
            //            logger.info("doReplicate end, appendEntriesResp: {}", JSONUtil.toJsonStr(appendEntriesResp));
            synchronized (queue) {
                if (appendEntriesResp.isSuccess()) {
                    if (_nextLogIndex > nextLogIndex) {
                        nextLogIndex = _nextLogIndex;
                    }
                    IdxAndFuture firstIdxAndFuture = queue.peek();
                    //                    logger.info("doReplicate end, firstIdxAndFuture: {}", JSONUtil.toJsonStr(firstIdxAndFuture));
                    if (firstIdxAndFuture != null && _nextLogIndex > firstIdxAndFuture.getIndex()) {
                        int length = _nextLogIndex - firstIdxAndFuture.getIndex();
                        IdxAndFuture idxAndFuture;
                        while (length > 0) {
                            idxAndFuture = queue.poll();
                            idxAndFuture.getReplicateFuture().incrementSuccess();
                            length--;
                        }
                    }
                }
                else {
                    if (_nextLogIndex < nextLogIndex) {
                        nextLogIndex = _nextLogIndex;
                        signalDoReplicate();
                    }
                }
            }
        }
    }
}


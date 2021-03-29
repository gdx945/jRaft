package io.github.gdx945.jraft.server.replicate;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.github.gdx945.util.CommonFuture;
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

    public Pipeline(String nodeId, RpcClient rpcClient, Supplier<Integer> leaderCommit, int replicationTimeout) {
        this.nodeId = nodeId;
        this.rpcClient = rpcClient;
        this.replicationTimeout = replicationTimeout;
        this.leaderCommit = leaderCommit;

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

    private final Queue<IdxAndFuture> queue = new PriorityBlockingQueue<>();

    private int nextLogIndex;

    private ExecutorService executorService = Executors.newFixedThreadPool(8, new DefaultThreadFactory("Pipeline"));

    private Lock doReplicateLock = new ReentrantLock();

    private Condition doReplicateLockCondition = doReplicateLock.newCondition();

    public void start() {
        this.nextLogIndex = this.logEntriesStore.getLastLogEntryIndex();
        signalDoReplicate();
    }

    public void stop() {
        this.nextLogIndex = 0;
    }

    public CommonFuture<Boolean> replicate(int index) {
        CommonFuture<Boolean> commonFuture = new CommonFuture<>();
//        logger.info("replicate index: {}", index);
        this.queue.add(new IdxAndFuture(index, commonFuture));
        start();
        return commonFuture;
    }

    private void doReplicate() {
        for (; ; ) {
            long timeMillis = System.currentTimeMillis();
            if (this.nextLogIndex != 0 && this.nextLogIndex <= this.logEntriesStore.getLastLogEntryIndex()) {
                AppendEntriesReq appendEntriesReq = new AppendEntriesReq();
                appendEntriesReq.setLeaderId(this.nodeId);
                appendEntriesReq.setTerm(this.nodeStateStore.getCurrentTerm());
                appendEntriesReq.setLeaderCommit(this.leaderCommit.get());

                List<LogEntry> logEntries;
                if (this.nextLogIndex == 1) {
                    logEntries = this.logEntriesStore.getList(1, 10);
                    appendEntriesReq.setEntries(logEntries);
                    appendEntriesReq.setPrevLogIndex(0);
                }
                else {
                    logEntries = this.logEntriesStore.getList(this.nextLogIndex - 1, 11);
                    LogEntry prevLog = logEntries.get(0);
                    appendEntriesReq.setEntries(new ArrayList<>(logEntries.subList(1, logEntries.size())));
                    appendEntriesReq.setPrevLogIndex(prevLog.getIndex());
                    appendEntriesReq.setPrevLogTerm(prevLog.getTerm());
                }
                //                logger.info("doReplicate: {} ~ {}", appendEntriesReq.getPrevLogIndex() + 1, logEntries.get(logEntries.size() - 1).getIndex());

                try {
                    InvokeFuture invokeFuture = rpcClient.invokeAsyn(ServerRpcMethod.APPEND_ENTRIES, appendEntriesReq, this.replicationTimeout);
                    this.nextLogIndex = logEntries.get(logEntries.size() - 1).getIndex() + 1;
                    executorService.submit(new ReplicateDone(invokeFuture, this.replicationTimeout - (System.currentTimeMillis() - timeMillis)));
                }
                catch (TimeoutException e) {
                    // throw ExceptionUtil.wrapRuntime(e);
                    // ignore
                }
            }
            else {
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
                            idxAndFuture.getCommonFuture().trySuccess(Boolean.TRUE);
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


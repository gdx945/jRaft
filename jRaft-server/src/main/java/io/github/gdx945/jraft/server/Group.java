package io.github.gdx945.jraft.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gdx945.jraft.server.model.LogEntry;
import io.github.gdx945.jraft.server.replicate.Pipeline;
import io.github.gdx945.jraft.server.rpc.method.ServerRpcMethod;
import io.github.gdx945.jraft.server.rpc.param.AppendEntriesReq;
import io.github.gdx945.jraft.server.rpc.param.AppendEntriesResp;
import io.github.gdx945.jraft.server.rpc.param.RequestVoteReq;
import io.github.gdx945.jraft.server.rpc.param.RequestVoteResp;
import io.github.gdx945.rpc.RpcClient;
import io.github.gdx945.util.CommonFuture;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-22 20:13:21
 * @since : 0.1
 */
class Group {

    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    private List<RpcClient> rpcClientList = new ArrayList<>();

    private List<Pipeline> replicatePipelineList = new ArrayList<>();

    private ThreadPoolExecutor requestVoteThreadPoolExecutor;

    private final ThreadFactory requestVoteThreadFactory = new DefaultThreadFactory("requestVote");

    private ThreadPoolExecutor sendHeartbeatThreadPoolExecutor;

    private final ThreadFactory sendHeartbeatThreadFactory = new DefaultThreadFactory("sendHeartbeat");

    private ThreadPoolExecutor appendEntriesThreadPoolExecutor;

    private final ThreadFactory appendEntriesThreadFactory = new DefaultThreadFactory("appendEntries");

    private final Integer[] leaderCommit = new Integer[] {0}; // todo

    private int replicationTimeout;

    private String nodeId;

    Group(String nodeId, String nodeAddrListStr, int replicationTimeout) {
        this.nodeId = nodeId;

        String[] nodeAddrList = nodeAddrListStr.split(";");
        for (String s : nodeAddrList) {
            String[] hostAndPort = s.split(":");
            this.rpcClientList.add(new RpcClient(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
            this.replicatePipelineList.add(
                new Pipeline(nodeId, new RpcClient(hostAndPort[0], Integer.parseInt(hostAndPort[1])), () -> leaderCommit[0], replicationTimeout));
        }

        this.replicationTimeout = replicationTimeout;
    }

    RequestVoteResp requestVote(RequestVoteReq requestVoteReq, int timeout) {
        int groupNodeCount = this.rpcClientList.size(); // groupNodeCount - 1
        CountDownLatch countDownLatch = new CountDownLatch(groupNodeCount);
        AtomicInteger voteCount = new AtomicInteger(0);

        RequestVoteResp result = new RequestVoteResp();
        result.setTerm(requestVoteReq.getTerm() - 1);
        result.setVoteGranted(false);

        for (RpcClient rpcClient : this.rpcClientList) {
            requestVoteThreadPoolExecutor().execute(() -> {
                RequestVoteResp requestVoteResp;
                try {
                    requestVoteResp = (RequestVoteResp) rpcClient.invoke(ServerRpcMethod.REQUEST_VOTE, requestVoteReq, timeout);
                }
                catch (Exception e) {
                    logger.debug("request vote from ".concat(rpcClient.getAddr()).concat(" failed."), e);
                    countDownLatch.countDown();
                    return;
                }

                synchronized (result) {
                    if (requestVoteResp.getTerm() > result.getTerm()) {
                        result.setTerm(requestVoteResp.getTerm());
                    }
                }
                if (requestVoteResp.isVoteGranted()) {
                    voteCount.incrementAndGet();
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        }
        catch (InterruptedException e) {
            logger.error("unknown", e);
            // 这个线程不会interrupted
        }

        if (voteCount.get() >= (groupNodeCount / 2)) {
            result.setVoteGranted(true);
        }

        return result;
    }

    AppendEntriesResp sendHeartbeat(AppendEntriesReq appendEntriesReq, int timeout) {
        AppendEntriesResp result = new AppendEntriesResp();
        result.setTerm(appendEntriesReq.getTerm());

        CountDownLatch countDownLatch = new CountDownLatch(this.rpcClientList.size());
        for (RpcClient rpcClient : rpcClientList) {
            sendHeartbeatThreadPoolExecutor().execute(() -> {
                AppendEntriesResp appendEntriesResp;
                try {
                    appendEntriesResp = (AppendEntriesResp) rpcClient.invoke(ServerRpcMethod.HEARTBEAT, appendEntriesReq, timeout);
                }
                catch (Exception e) {
                    logger.debug("send heartbeat to ".concat(rpcClient.getAddr()).concat(" failed."), e);
                    countDownLatch.countDown();
                    return;
                }

                synchronized (result) {
                    if (appendEntriesResp.getTerm() > result.getTerm()) {
                        // 心跳时发现其它node term更高
                        result.setTerm(appendEntriesResp.getTerm());
                    }
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        }
        catch (InterruptedException e) {
            logger.error("unknown", e);
            // 这个线程不会interrupted
        }

        return result;
    }

    void startReplicate() {
        for (Pipeline pipeline : this.replicatePipelineList) {
            pipeline.start();
        }
    }

    void stopReplicate() {
        for (Pipeline pipeline : this.replicatePipelineList) {
            pipeline.stop();
        }
    }

    boolean appendEntries(LogEntry logEntry) {
        int halfFollowerCount = this.replicatePipelineList.size() / 2;
        CountDownLatch countDownLatch = new CountDownLatch(halfFollowerCount);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        int index = logEntry.getIndex();
        for (Pipeline pipeline : this.replicatePipelineList) {
            appendEntriesThreadPoolExecutor().execute(() -> {
                CommonFuture<Boolean> commonFuture = pipeline.replicate(index);
                try {
                    if (Boolean.TRUE.equals(commonFuture.get(replicationTimeout - 10, TimeUnit.MILLISECONDS))) {
                        atomicInteger.incrementAndGet();
                        countDownLatch.countDown();
                    }
                }
                catch (Exception e) {
                    logger.debug("append entries sync to ".concat(pipeline.getAddr()).concat(" failed."), e);
                }
            });
        }

        try {
            countDownLatch.await(this.replicationTimeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            logger.error("unknown", e); // 这个线程暂时没有interrupted
        }

        boolean result = false;
        if (atomicInteger.get() >= halfFollowerCount) {
            result = true;
            synchronized (leaderCommit) {
                leaderCommit[0] = index;
            }
        }
        return result;
    }

    private ThreadPoolExecutor requestVoteThreadPoolExecutor() {
        int groupNodeCount = this.rpcClientList.size(); // groupNodeCount - 1
        if (this.requestVoteThreadPoolExecutor == null || this.requestVoteThreadPoolExecutor.getCorePoolSize() != groupNodeCount) {
            this.requestVoteThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(groupNodeCount, this.requestVoteThreadFactory);
        }
        return this.requestVoteThreadPoolExecutor;
    }

    private ThreadPoolExecutor sendHeartbeatThreadPoolExecutor() {
        int groupNodeCount = this.rpcClientList.size(); // groupNodeCount - 1
        if (this.sendHeartbeatThreadPoolExecutor == null || this.sendHeartbeatThreadPoolExecutor.getCorePoolSize() != groupNodeCount) {
            this.sendHeartbeatThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(groupNodeCount, this.sendHeartbeatThreadFactory);
        }
        return this.sendHeartbeatThreadPoolExecutor;
    }

    private ThreadPoolExecutor appendEntriesThreadPoolExecutor() {
        int groupNodeCount = this.replicatePipelineList.size(); // groupNodeCount - 1
        if (this.appendEntriesThreadPoolExecutor == null || this.appendEntriesThreadPoolExecutor.getCorePoolSize() != groupNodeCount) {
            this.appendEntriesThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(groupNodeCount, this.appendEntriesThreadFactory);
        }
        return this.appendEntriesThreadPoolExecutor;
    }
}

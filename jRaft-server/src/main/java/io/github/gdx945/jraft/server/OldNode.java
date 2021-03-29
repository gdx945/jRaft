package io.github.gdx945.jraft.server;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.json.JSONUtil;
import io.github.gdx945.jraft.common.param.AddLogEntryReq;
import io.github.gdx945.jraft.common.param.AddLogEntryResp;
import io.github.gdx945.jraft.common.rpc.CommonRpcMethod;
import io.github.gdx945.jraft.server.model.LogEntry;
import io.github.gdx945.jraft.server.option.NodeOptions;
import io.github.gdx945.jraft.server.rpc.method.ServerRpcMethod;
import io.github.gdx945.jraft.server.rpc.param.AppendEntriesReq;
import io.github.gdx945.jraft.server.rpc.param.AppendEntriesResp;
import io.github.gdx945.jraft.server.rpc.param.RequestVoteReq;
import io.github.gdx945.jraft.server.rpc.param.RequestVoteResp;
import io.github.gdx945.jraft.server.store.LogEntriesStore;
import io.github.gdx945.jraft.server.store.NodeStateStore;
import io.github.gdx945.jraft.statemachine.StateMachine;
import io.github.gdx945.jraft.statemachine.impl.DefaultStateMachine;
import io.github.gdx945.rpc.RpcService;
import io.github.gdx945.util.RepeatedTimer;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * raft节点
 *
 * @author : gc
 * Created on 2021-02-22 11:25:26
 * @since : 0.1
 */
public class OldNode {

    private static final Logger logger = LoggerFactory.getLogger(OldNode.class);

    private static final Map<String, OldNode> NODE_MAP = new ConcurrentHashMap<>();

    public static OldNode getNodeById(String nodeId) {
        return NODE_MAP.get(nodeId);
    }

    public OldNode(NodeOptions nodeOptions) {
        this.nodeOptions = nodeOptions;
        this.nodeId = this.nodeOptions.getNodeId();
        NODE_MAP.put(this.nodeId, this);

        this.init();
    }

    public LogEntriesStore getLogEntriesStore() {
        return logEntriesStore;
    }

    public NodeStateStore getNodeStateStore() {
        return nodeStateStore;
    }

    private NodeOptions nodeOptions;

    /**
     * 实现选举
     */
    private String nodeId;

    // 需要持久化的 currentTerm、 votedFor、 logEntries

    private final Lock votedForLock = new ReentrantLock();

    private RepeatedTimer electionTimer;

    private RepeatedTimer heartbeatTimer;

    private int electionTimeout;

    private int heartbeatDelay;

    private Group group;

    private String leaderNodeId;

    private NodeStateStore nodeStateStore;

    private LogEntriesStore logEntriesStore;

    private StateMachine stateMachine;

    private void init() {
        this.stateMachine = Optional.ofNullable(ServiceLoaderUtil.loadFirst(StateMachine.class)).orElse(new DefaultStateMachine());

        this.nodeStateStore = new NodeStateStore(this.nodeOptions.getStorePath());
        this.logEntriesStore = new LogEntriesStore(this.nodeOptions.getStorePath());

        new RpcService(nodeOptions.getPort()).putServiceHandler(ServerRpcMethod.REQUEST_VOTE, o -> this.handleVoteRequest((RequestVoteReq) o))
            .putServiceHandler(ServerRpcMethod.HEARTBEAT, o -> this.handleHeartbeat((AppendEntriesReq) o))
            .putServiceHandler(ServerRpcMethod.APPEND_ENTRIES, o -> this.handleAppendEntries((AppendEntriesReq) o),
                Executors.newFixedThreadPool(1, new DefaultThreadFactory("appendEntries")))
            .putServiceHandler(CommonRpcMethod.ADD_LOG_ENTRY, o -> this.addLogEntry((AddLogEntryReq) o));

        this.group = new Group(this.nodeId, this.nodeOptions.getOtherNodeAddr(), this.nodeOptions.getElectionTimeout());

        this.electionTimeout = this.nodeOptions.getElectionTimeout();
        this.electionTimer = new RepeatedTimer(this.electionTimeout, this.electionTimeout) {
            @Override
            protected void onTrigger() {
                electSelf();
            }
        };
        this.electionTimer.start();

        this.heartbeatDelay = this.nodeOptions.getElectionTimeout() / 2;
        this.heartbeatTimer = new RepeatedTimer(this.heartbeatDelay) {
            @Override
            protected void onTrigger() {
                sendHeartbeat();
            }
        };
    }

    // **    选举     ** //
    private void electSelf() {
        logger.info("electSelf start, term: {}", this.nodeStateStore.getCurrentTerm());

        this.nodeStateStore.setVotedFor(null);

        // 请求选票
        RequestVoteReq requestVoteReq = new RequestVoteReq();
        requestVoteReq.setCandidateId(nodeId);
        requestVoteReq.setTerm(this.nodeStateStore.getCurrentTerm() + 1);
        LogEntry lastLogEntry = this.logEntriesStore.getLastLogEntry();
        requestVoteReq.setLastLogIndex(lastLogEntry.getIndex());
        requestVoteReq.setLastLogTerm(lastLogEntry.getTerm());
        // 随机延迟
        int randomDelay = RandomUtil.randomInt(0, this.electionTimeout / 2);
        try {
            TimeUnit.MILLISECONDS.sleep(randomDelay);
        }
        catch (InterruptedException e) {
            // ignore
        }

        // 先投票给自己
        if (setVotedFor(this.nodeId)) {
            RequestVoteResp requestVoteResp = this.group.requestVote(requestVoteReq, this.electionTimeout - (randomDelay * 2) - 10);
            logger.info("electSelf end, highest term: {}", this.nodeStateStore.getCurrentTerm());

            // 处理请求选票的结果
            this.nodeStateStore.setCurrentTerm(requestVoteResp.getTerm());
            if (requestVoteResp.isVoteGranted()) {
                becomeLeader();
            }

            this.nodeStateStore.setVotedFor(null);
        }
    }

    private void becomeLeader() {
        this.leaderNodeId = this.nodeId;
        this.electionTimer.cancel();
        this.heartbeatTimer.start();
        this.group.startReplicate();
        logger.info("I'm leader {}, term {}", this.nodeId, this.nodeStateStore.getCurrentTerm());
    }

    /**
     * @param requestVoteReq
     * @return
     */
    private RequestVoteResp handleVoteRequest(RequestVoteReq requestVoteReq) {
        boolean voteGranted = false;

        if (requestVoteReq.getTerm() > this.nodeStateStore.getCurrentTerm()) {
            if (this.nodeStateStore.getVotedFor() == null || this.nodeStateStore.getVotedFor().equals(requestVoteReq.getCandidateId())) {
                LogEntry lastLogEntry = this.logEntriesStore.getLastLogEntry();
                long lastLogEntryIdx = lastLogEntry.getIndex();
                logger.info("handleVoteRequest: last log term: {}, last log idx: {}", lastLogEntry.getTerm(), lastLogEntryIdx);
                if (requestVoteReq.getLastLogTerm() > lastLogEntry.getTerm() || (requestVoteReq.getLastLogTerm() == lastLogEntry.getTerm()
                    && requestVoteReq.getLastLogIndex() >= lastLogEntryIdx)) {

                    if (setVotedFor(requestVoteReq.getCandidateId())) {
                        this.electionTimer.restart();
                        this.group.stopReplicate();
                        voteGranted = true;
                    }
                }
            }
        }

        RequestVoteResp result = new RequestVoteResp();
        result.setTerm(this.nodeStateStore.getCurrentTerm());
        result.setVoteGranted(voteGranted);
        return result;
    }

    private void sendHeartbeat() {
        //        logger.info("sendHeartbeat start, term: {}", this.nodeStateStore.getCurrentTerm());
        AppendEntriesReq appendEntriesReq = new AppendEntriesReq();
        appendEntriesReq.setTerm(this.nodeStateStore.getCurrentTerm());
        appendEntriesReq.setLeaderId(this.nodeId);

        AppendEntriesResp appendEntriesResp = this.group.sendHeartbeat(appendEntriesReq, (this.heartbeatDelay / 2) - 5);
        this.nodeStateStore.setCurrentTerm(appendEntriesResp.getTerm());
    }

    /**
     * 收到心跳后
     * 1、term >= currentTerm 重置选举超时器, currentTerm = term, this.votedFor[0] = null
     *
     * @param appendEntriesReq
     * @return
     */
    private AppendEntriesResp handleHeartbeat(AppendEntriesReq appendEntriesReq) {
        boolean success = false;
        if (this.nodeStateStore.getCurrentTerm() <= appendEntriesReq.getTerm()) {
            this.leaderNodeId = appendEntriesReq.getLeaderId();
            this.electionTimer.restart();
            this.nodeStateStore.setCurrentTerm(appendEntriesReq.getTerm());
            if (appendEntriesReq.getLeaderId().equals(this.nodeStateStore.getVotedFor())) {
                this.nodeStateStore.setVotedFor(null);
            }
            success = true;
        }

        AppendEntriesResp result = new AppendEntriesResp();
        result.setTerm(this.nodeStateStore.getCurrentTerm());
        result.setSuccess(success);
        return result;
    }

    private boolean setVotedFor(String votedFor) {
        boolean result = false;
        if (votedForLock.tryLock()) {
            if (this.nodeStateStore.getVotedFor() == null || this.nodeStateStore.getVotedFor().equals(votedFor)) {
                this.nodeStateStore.setVotedFor(votedFor);
                result = true;
            }
            votedForLock.unlock();
        }
        return result;
    }
    // **    选举     ** //

    // **    日志同步     ** //
    private AddLogEntryResp addLogEntry(AddLogEntryReq addLogEntryReq) {
        long time = System.currentTimeMillis();

        AddLogEntryResp result = new AddLogEntryResp();
        result.setLeaderNodeId(this.leaderNodeId);
        result.setNodeId(this.nodeId);
        if (addLogEntryReq == null || addLogEntryReq.getLogEntry() == null || !this.nodeId.equals(this.leaderNodeId)) {
            result.setIndex(-1L);
        }
        else {
            LogEntry logEntryForAdd = new LogEntry(this.nodeStateStore.getCurrentTerm(), -1, addLogEntryReq.getLogEntry().getCommand(),
                addLogEntryReq.getLogEntry().getParam());
            this.logEntriesStore.addLogEntries(logEntryForAdd);
            result.setIndex(logEntryForAdd.getIndex());

            this.group.appendEntries(logEntryForAdd);
        }

        System.out.println(System.currentTimeMillis() - time);
        return result;
    }

    private AppendEntriesResp handleAppendEntries(AppendEntriesReq appendEntriesReq) {
        AppendEntriesResp result = handleHeartbeat(appendEntriesReq);
        if (result.isSuccess()) {
            result.setSuccess(false);
            logger.info("handleAppendEntries:  {}", JSONUtil.toJsonStr(appendEntriesReq));
            if (appendEntriesReq.getPrevLogIndex() == 0) {
                result.setSuccess(true);
                this.logEntriesStore.appendEntries(appendEntriesReq.getPrevLogIndex() + 1, appendEntriesReq.getEntries());
                result.setNextLogIndex(this.logEntriesStore.getLastLogEntryIndex() + 1);
            }
            else {
                LogEntry prevLogEntry = CollUtil.getFirst(this.logEntriesStore.getList(appendEntriesReq.getPrevLogIndex(), 1));
                if (prevLogEntry != null) {
                    logger.info("handleAppendEntries:  {}, {}, {}, {}", prevLogEntry.getIndex(), appendEntriesReq.getPrevLogIndex(),
                        prevLogEntry.getTerm(), appendEntriesReq.getPrevLogTerm());
                    if (prevLogEntry.getTerm() == appendEntriesReq.getPrevLogTerm()) {
                        result.setSuccess(true);
                        this.logEntriesStore.appendEntries(appendEntriesReq.getPrevLogIndex() + 1, appendEntriesReq.getEntries());
                        result.setNextLogIndex(this.logEntriesStore.getLastLogEntryIndex() + 1);
                    }
                    else {
                        result.setNextLogIndex(appendEntriesReq.getPrevLogIndex()); // 往后倒
                    }
                }
                else {
                    result.setNextLogIndex(this.logEntriesStore.getLastLogEntryIndex() + 1); // 到末尾
                }
            }
        }
        logger.info("handleAppendEntries: success: {}, nextLogIndex: {}", result.isSuccess(), result.getNextLogIndex());
        return result;
    }
}

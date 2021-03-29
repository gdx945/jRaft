package io.github.gdx945.jraft.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gdx945.jraft.client.option.RaftClientOptions;
import io.github.gdx945.jraft.common.param.AddLogEntryReq;
import io.github.gdx945.jraft.common.param.AddLogEntryResp;
import io.github.gdx945.jraft.common.param.LogEntry;
import io.github.gdx945.jraft.common.rpc.CommonRpcMethod;
import io.github.gdx945.rpc.InvokeFuture;
import io.github.gdx945.rpc.RpcClient;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-02 10:03:27
 * @since : 0.1
 */
public class RaftClient {

    private static final Logger logger = LoggerFactory.getLogger(RaftClient.class);

    public RaftClient(RaftClientOptions raftClientOptions) {
        for (String s : raftClientOptions.getNodeAddrList().split(";")) {
            String[] hostAndPort = s.split(":");
            this.rpcClientList.add(new RpcClient(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }
        getNodesName();
    }

    private List<RpcClient> rpcClientList = new ArrayList<>();

    private Map<String, RpcClient> rpcClientMap = new ConcurrentHashMap<>();

    private String leaderNodeId;

    private InvokeFuture<String> getNodesName() {
        int size = this.rpcClientList.size();
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<String> leaderNodeIds = Collections.synchronizedList(new ArrayList<>());
        InvokeFuture<String> invokeFuture = new InvokeFuture<>();
        for (RpcClient rpcClient : this.rpcClientList) {
            executorService.submit(() -> {
                try {
                    AddLogEntryResp resp = (AddLogEntryResp) rpcClient.invoke(CommonRpcMethod.ADD_LOG_ENTRY, null, 1000); // todo
                    this.rpcClientMap.put(resp.getNodeId(), rpcClient);
                    leaderNodeIds.add(resp.getLeaderNodeId());
                }
                catch (Exception e) {
                    // ignore
                    logger.error("", e);
                }
                finally {
                    if (atomicInteger.incrementAndGet() == size) {
                        leaderNodeIds.stream().collect(Collectors.groupingBy(String::toString, Collectors.counting())).entrySet().stream()
                            .filter(stringLongEntry -> stringLongEntry.getValue() > (size / 2)).findFirst()
                            .ifPresent(stringLongEntry -> this.leaderNodeId = stringLongEntry.getKey());
                        logger.info("leader node id: {}", this.leaderNodeId);
                        invokeFuture.trySuccess(this.leaderNodeId);
                    }
                }
            });
        }
        executorService.shutdown();
        return invokeFuture;
    }

    public void addLogEntry(LogEntry logEntry) {
        String leaderNodeId = this.leaderNodeId;
        if (leaderNodeId == null || this.rpcClientMap.get(leaderNodeId) == null) {
            InvokeFuture<String> invokeFuture = getNodesName();
            try {
                leaderNodeId = invokeFuture.get(1000L, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                // ignore
            }
        }

        if (leaderNodeId == null) {
            System.out.println("no leader");
            return;
        }

        RpcClient rpcClient = this.rpcClientMap.get(leaderNodeId);
        if (rpcClient != null) {
            AddLogEntryReq addLogEntryReq = new AddLogEntryReq();
            addLogEntryReq.setLogEntry(logEntry);
            try {
                AddLogEntryResp addLogEntryResp = (AddLogEntryResp) rpcClient.invoke(CommonRpcMethod.ADD_LOG_ENTRY, addLogEntryReq, 2000L);
                this.leaderNodeId = addLogEntryResp.getLeaderNodeId();
                //                logger.info("index: {}, leader node id: {}", addLogEntryResp.getIndex(),
                //                    addLogEntryResp.getLeaderNodeId());
                if (!addLogEntryResp.getLeaderNodeId().equals(addLogEntryResp.getNodeId())) {
                    addLogEntry(logEntry);
                }
            }
            catch (TimeoutException e) {
                this.leaderNodeId = null;
                logger.info("add log entry timeout.", e);
            }
        }
    }
}

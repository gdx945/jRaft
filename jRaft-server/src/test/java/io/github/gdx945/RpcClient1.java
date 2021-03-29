package io.github.gdx945;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.gdx945.jraft.common.rpc.CommonRpcMethod;
import io.github.gdx945.jraft.server.model.LogEntry;
import io.github.gdx945.jraft.server.rpc.method.ServerRpcMethod;
import io.github.gdx945.jraft.server.rpc.param.AppendEntriesReq;
import io.github.gdx945.protobuf.Any;
import io.github.gdx945.protobuf.Map;
import io.github.gdx945.rpc.RpcClient;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-23 19:45:44
 * @since : 0.1
 */
public class RpcClient1 {

    public static void main(String[] args) throws InterruptedException {
        RpcClient rpcClient = new RpcClient("127.0.0.1", 4444);

        int count = 8;
        int count2 = 1;
        RpcClient[] rpcClients = new RpcClient[count];
        for (int i = 0; i < count; i++) {
            rpcClients[i] = new RpcClient("127.0.0.1", 4444);
        }

        TimeUnit.SECONDS.sleep(1);

        ExecutorService executorService = Executors.newFixedThreadPool(count * count2);
        CountDownLatch countDownLatch = new CountDownLatch(count * count2);

        LogEntry logEntry = new LogEntry(0, -1, "command", "param");
        long time = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count2; j++) {
                int finalI = i;
                executorService.submit(() -> {
                    for (int x = 0; x < 8192; x++) {
                        //                        raftClientList[finalI].addLogEntry(new LogEntry("add", map));
                        try {
                            rpcClients[finalI].invoke(CommonRpcMethod.ADD_LOG_ENTRY, logEntry, 500);
                        }
                        catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                    countDownLatch.countDown();
                });
            }
        }

        countDownLatch.await();
        System.out.println("cost: " + (System.currentTimeMillis() - time));
    }
}

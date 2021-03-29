package io.github.gdx945;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.github.gdx945.jraft.client.RaftClient;
import io.github.gdx945.jraft.client.option.RaftClientOptions;
import io.github.gdx945.jraft.common.param.LogEntry;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-03 15:05:26
 * @since : 0.1
 */
public class RaftClient1 {

    public static void main(String[] args) throws InterruptedException {
        RaftClientOptions raftClientOptions = new RaftClientOptions();
        raftClientOptions.setNodeAddrList("127.0.0.1:1111;127.0.0.1:2222;127.0.0.1:3333");

        int count = 8;
        int count2 = 1;
        RaftClient[] raftClientList = new RaftClient[count];
        for (int i = 0; i < count; i++) {
            raftClientList[i] = new RaftClient(raftClientOptions);
        }
        RaftClient raftClient = new RaftClient(raftClientOptions);

        TimeUnit.SECONDS.sleep(1);
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("a", "A");

        ExecutorService executorService = Executors.newFixedThreadPool(count * count2);
        CountDownLatch countDownLatch = new CountDownLatch(count * count2);
        long time = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count2; j++) {
                int finalI = i;
                executorService.submit(() -> {
                    for (int x = 0; x < 8192; x++) {
                        raftClientList[finalI].addLogEntry(new LogEntry("add", map));
//                        raftClientList[finalI].addLogEntry(null);
                    }
                    countDownLatch.countDown();
                });
            }
        }

        countDownLatch.await();
        System.out.println("cost: " + (System.currentTimeMillis() - time));
    }
}

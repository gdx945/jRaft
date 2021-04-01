package io.github.gdx945;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.gdx945.jraft.server.model.LogEntry;
import io.github.gdx945.jraft.server.store.LogEntriesStore;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-06 17:00:07
 * @since : 0.1
 */
public class TestLogEntriesStore {

    public static void main(String[] args) {
        //        byte[] bytes = NumberUtil.toBytes(2147483648L, 4);
        //        int in = cn.hutool.core.util.NumberUtil.toInt(bytes);
        //        long lo = NumberUtil.toLong(bytes);
        //        System.out.println((1 << 31));
        //        System.out.println((long) 1 << 31);
        //        System.out.println(-128 & 255);

        LogEntriesStore logEntriesStore = new LogEntriesStore("/Users/gc/Workpath/0自己代码项目/jRaft-parent-store/node1");

        int i = 8;
        CountDownLatch countDownLatch = new CountDownLatch(i);
        ExecutorService executorService = Executors.newFixedThreadPool(i);
        long startTime = System.currentTimeMillis();

        while (i-- > 0) {
            executorService.submit(() -> {
                add(logEntriesStore);
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - startTime);
        executorService.shutdown();

        List<LogEntry> logEntry1 = logEntriesStore.getList(12, 10);
        LogEntry logEntry = logEntriesStore.getLastLogEntry();
        int _i = 1;

        //        NodeStateStore nodeStateStore = new NodeStateStore("/Users/gc/Workpath/0自己代码项目/jRaft-parent-store/node1");
        //        System.out.println(nodeStateStore.getCurrentTerm());
        //        System.out.println(nodeStateStore.getVotedFor());
        //        //        for (int i = 0; i < 10; i++) {
        //                    nodeStateStore.setCurrentTerm(RandomUtil.randomInt(100));
        //                    nodeStateStore.setVotedFor(RandomUtil.randomString(RandomUtil.randomInt(10)));
        //                    nodeStateStore.setVotedFor(null);
        //        //        }
        //        System.out.println(nodeStateStore.getCurrentTerm());
        //        System.out.println(nodeStateStore.getVotedFor());
    }

    private static void add(LogEntriesStore logEntriesStore) {
        for (int i = 1; i <= 8192; i++) {
            LogEntry logEntry = new LogEntry(i, 0, "command", "param");
//            logEntriesStore.addLogEntries(logEntry, null);
            logEntriesStore.addLogEntries(logEntry);
        }
    }

}

package io.github.gdx945;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.thread.ThreadUtil;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-01 16:54:31
 * @since : 0.1
 */
public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    static ScheduledFuture<?> scheduledFuture;

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        final int[] i = {1};
        //        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
        //            try {
        //                if (i[0] == 5) {
        //                    Thread.sleep(500);
        //                    scheduledFuture.cancel(false);
        //                }
        //            }
        //            catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //            logger.info("xx");
        //            i[0]++;
        //        }, 0, 100, TimeUnit.MILLISECONDS);

//        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
//            logger.info("xx");
//            ThreadUtil.sleep(500);
//            logger.info("xxx");
//            if (i[0] == 5) {
//                scheduledFuture.cancel(false);
//            }
//            i[0]++;
//        }, 0, 600, TimeUnit.MILLISECONDS);
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            logger.info("yy");
//        }, 300, 600, TimeUnit.MILLISECONDS);

        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
        hashedWheelTimer.newTimeout(timeout -> System.out.println("xxx"), 400, TimeUnit.MILLISECONDS);
    }
}

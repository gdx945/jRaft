package io.github.gdx945.util;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-22 18:41:07
 * @since : 0.1
 */
public abstract class Timeout {

    private long delay;

    protected Timeout(long delay) {
        this.delay = delay;
    }

    private ScheduledFuture scheduledFuture;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    protected abstract void onTimeout();

    public void start() {
        this.scheduledFuture = scheduledThreadPoolExecutor.schedule(this::onTimeout, this.delay, TimeUnit.MILLISECONDS);
    }

    public void cancel() {
        this.scheduledFuture.cancel(false);
    }

    public void restart() {
        cancel();
        start();
    }

}

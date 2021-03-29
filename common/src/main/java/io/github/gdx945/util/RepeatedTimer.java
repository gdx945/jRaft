package io.github.gdx945.util;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-23 13:52:08
 * @since : 0.1
 */
public abstract class RepeatedTimer {

    private long delay;

    private long initialDelay;

    public RepeatedTimer(long delay) {
        this(delay, 0);
    }

    public RepeatedTimer(long delay, long initialDelay) {
        this.delay = delay;
        this.initialDelay = initialDelay;
    }

    private RunnableScheduledFuture runnableScheduledFuture;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("RepeatedTimer"));

    public void start() {
        synchronized (this) {
            if (this.runnableScheduledFuture == null) {
                this.runnableScheduledFuture = (RunnableScheduledFuture) scheduledThreadPoolExecutor
                    .scheduleAtFixedRate(this::onTrigger, initialDelay, delay, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void cancel() {
        synchronized (this) {
            if (this.runnableScheduledFuture != null) {
                this.runnableScheduledFuture.cancel(false);
                this.runnableScheduledFuture = null;
            }
        }
    }

    public void restart() {
        cancel();
        start();
    }

    protected abstract void onTrigger();
}

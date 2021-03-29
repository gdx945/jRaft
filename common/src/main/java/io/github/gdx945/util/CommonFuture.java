package io.github.gdx945.util;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-02-25 15:55:55
 * @since : 0.1
 */
public class CommonFuture<V extends Serializable> {

    private static final Logger logger = LoggerFactory.getLogger(CommonFuture.class);

    /**
     * 装饰者模式
     */
    private DefaultPromise<V> defaultPromise = new DefaultPromise<>();

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return defaultPromise.get(timeout, unit);
    }

    public boolean trySuccess(V result) {
        return defaultPromise.trySuccess(result);
    }

    private static class DefaultPromise<V> extends io.netty.util.concurrent.DefaultPromise<V> {
        DefaultPromise() {
            super(GlobalEventExecutor.INSTANCE);
        }
    }
}

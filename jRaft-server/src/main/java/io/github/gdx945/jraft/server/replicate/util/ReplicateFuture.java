package io.github.gdx945.jraft.server.replicate.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gdx945.jraft.common.param.AddLogEntryResp;
import io.github.gdx945.util.CommonFuture;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * 类描述
 *
 * @author : gc
 * Created on 2021-03-30 18:37:23
 * @since : 0.1
 */
public class ReplicateFuture extends CommonFuture<AddLogEntryResp> {

    private static final Logger logger = LoggerFactory.getLogger(ReplicateFuture.class);

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(1, new DefaultThreadFactory("ReplicateFuture"));

    public ReplicateFuture(int successCount, Supplier<AddLogEntryResp> getResult) {
        this.successCount = successCount;
        this.getResult = getResult;
    }

    private int successCount;

    private Supplier<AddLogEntryResp> getResult;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private CommonFuture<AddLogEntryResp> commonFuture = new CommonFuture<>();

    public void incrementSuccess() {
        EXECUTOR_SERVICE.submit(() -> {
            if (this.atomicInteger.incrementAndGet() == this.successCount) {
                AddLogEntryResp addLogEntryResp = this.getResult.get();
                //                logger.info("{}", addLogEntryResp.getIndex());
                this.commonFuture.trySuccess(addLogEntryResp);
            }
        });
    }

    public AddLogEntryResp get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.commonFuture.get(timeout, unit);
    }

    public boolean trySuccess(AddLogEntryResp result) {
        throw new UnsupportedOperationException("please use incrementSuccess()");
    }
}

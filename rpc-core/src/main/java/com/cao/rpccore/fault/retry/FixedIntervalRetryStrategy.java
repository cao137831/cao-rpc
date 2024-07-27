package com.cao.rpccore.fault.retry;

import com.cao.rpccore.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定间隔时间重试
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    // todo Callable的使用
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(1L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("当前调用的轮数为：{}", attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}

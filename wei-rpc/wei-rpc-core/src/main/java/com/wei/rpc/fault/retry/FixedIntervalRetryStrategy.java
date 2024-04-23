package com.wei.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.wei.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔 - 重试策略
 *

 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * 重试
     *
     * @param callable
     * @return
     * @throws ExecutionException
     * @throws RetryException
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                //指定什么异常时重试
                .retryIfExceptionOfType(Exception.class)
                //指定等待重试策略，固定时间重试
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                //指定重试停止策略，超过最大次数停止
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                //重试工作，
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }

}

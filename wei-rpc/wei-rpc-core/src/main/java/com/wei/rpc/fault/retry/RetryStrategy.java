package com.wei.rpc.fault.retry;

import com.wei.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}

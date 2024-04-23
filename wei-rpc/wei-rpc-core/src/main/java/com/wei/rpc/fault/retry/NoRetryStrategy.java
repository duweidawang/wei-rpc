package com.wei.rpc.fault.retry;

import com.wei.rpc.model.RpcResponse;

import java.util.concurrent.Callable;


//不重试
public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}

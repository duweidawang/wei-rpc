package com.wei.rpc.fault.tolerant;

import com.wei.rpc.model.RpcResponse;

import java.util.Map;

public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //调用降级服务
        return null;
    }
}

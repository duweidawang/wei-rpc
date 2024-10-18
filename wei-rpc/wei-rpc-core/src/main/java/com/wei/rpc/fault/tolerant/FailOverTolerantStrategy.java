package com.wei.rpc.fault.tolerant;

import com.wei.rpc.loadbalancer.LoadBalancer;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.server.netty.NettyClient;

import java.util.List;
import java.util.Map;

/**
 * 故障转移
 * 试一下其他几点是否可以调用，如果都不可以直接排除异常
 */
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception {
        List<ServiceMetaInfo> server = (List<ServiceMetaInfo>) context.get("server");
        ServiceMetaInfo serviceMetaInfo = (ServiceMetaInfo) context.get("currentServer");
        RpcRequest rpcRequest = (RpcRequest) context.get("request");
        LoadBalancer loadBalancer = (LoadBalancer) context.get("loadbalancer");
        RpcResponse response=null;
        while(server.size()>0){
            boolean remove = server.remove(serviceMetaInfo);
            try {
                response = NettyClient.doRequest(rpcRequest, server.get(0));
                if(response.getExcption()!=null){
                    throw new RuntimeException();
                }
            } catch (Exception exception) {
               server.remove(server.get(0));
            }
        }
        //如果全部都调用完毕了，依然抛出异常            这里是不是可以熔断，对接口名字外单位进行熔断
        if(response.getExcption()!=null){

            //将接口名字，放入一个黑名单

        }
        //如果成功了，返回响应
        return response;
    }
}

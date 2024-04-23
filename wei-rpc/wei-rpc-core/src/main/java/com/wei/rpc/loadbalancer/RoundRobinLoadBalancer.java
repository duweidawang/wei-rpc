package com.wei.rpc.loadbalancer;

import com.wei.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡轮询
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    //当前轮询的下标 使用原子类int类型
    private final AtomicInteger currentIndex = new AtomicInteger(0);


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        //没有服务
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        //如果大小为1，只有一个服务，无需轮询
        int size = serviceMetaInfoList.size();
        if(size==1){
            return serviceMetaInfoList.get(0);
        }
        //取模算法来轮询
        int i = currentIndex.getAndIncrement() % size;
        System.out.println("轮循到了"+i);
        return serviceMetaInfoList.get(i);

    }
}

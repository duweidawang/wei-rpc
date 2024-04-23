package com.wei.rpc.loadbalancer;

import com.wei.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡器
 */
public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
            if (serviceMetaInfoList.isEmpty()){
                return null;
            }
        int size = serviceMetaInfoList.size();
            if(size == 1){
                return serviceMetaInfoList.get(0);
            }
            return serviceMetaInfoList.get(new Random().nextInt(size));
    }
}

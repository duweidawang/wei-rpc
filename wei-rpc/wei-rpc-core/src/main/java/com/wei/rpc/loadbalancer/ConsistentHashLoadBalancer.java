package com.wei.rpc.loadbalancer;

import com.wei.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes =new TreeMap<>();
    //虚拟节点的数量
    private static final int VIRTUAL_NODE = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {

        if(serviceMetaInfoList.isEmpty()) return null;

        //构建虚拟节点
        for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() +"#"+i);
                virtualNodes.put(hash,serviceMetaInfo);

            }
        }

        //获取调用请求的hash值
        int hash = getHash(requestParams);
        //选择最接近且大于等于hash值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> integerServiceMetaInfoEntry = virtualNodes.ceilingEntry(hash);
        if(integerServiceMetaInfoEntry==null){
            integerServiceMetaInfoEntry = virtualNodes.firstEntry();
        }
        return integerServiceMetaInfoEntry.getValue();



    }

    private int getHash(Object key){
        return key.hashCode();
    }
}
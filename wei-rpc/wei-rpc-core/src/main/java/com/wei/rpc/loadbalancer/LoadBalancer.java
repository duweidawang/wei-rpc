package com.wei.rpc.loadbalancer;

import com.wei.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;


/**
 * 服务选择方法接口
 */
public interface LoadBalancer {

    ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}

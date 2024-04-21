package com.wei.rpc.registry;

import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.model.ServiceMetaInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Registry {


        /**
         * 初始化
         *
         * @param registryConfig
         */
        void init(RegistryConfig registryConfig);

        /**
         * 注册服务（服务端）
         *
         * @param serviceMetaInfo
         */
        void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

        /**
         * 注销服务（服务端）
         *
         * @param serviceMetaInfo
         */
        void unRegister(ServiceMetaInfo serviceMetaInfo);

        /**
         * 服务发现（获取某服务的所有节点，消费端）
         *
         * @param serviceKey 服务键名
         * @return
         */
        List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

        /**
         * 心跳检测（服务端）
         */
        void heartBeat();

        /**
         * 监听（消费端）
         *
         * @param serviceNodeKey
         */
        void watch(String serviceNodeKey);

        /**
         * 服务销毁
         */
        void destroy();
    }




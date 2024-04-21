package com.wei.rpc.config;

import com.wei.rpc.constant.SerializerConstatnt;
import lombok.Data;

/**
 * Rpc框架配置
 */
@Data
public class RpcConfig {

    //名称
    private String name = "wei-rpc";
    //版本
    private String version = "1.0";
    //主机名
    private String serverHost = "localhost";
    //端口号
    private Integer serverPort = 8080;

    //序列化器
    private String serializer = SerializerConstatnt.JDK;

    //注册中心的配置
    private RegistryConfig registryConfig = new RegistryConfig();
}

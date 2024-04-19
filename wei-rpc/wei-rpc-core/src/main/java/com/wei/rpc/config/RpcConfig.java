package com.wei.rpc.config;

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
}

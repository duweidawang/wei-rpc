package com.wei.rpc.config;

import lombok.Data;

/**
 * PRC注册中心配置
 */
@Data
public class RegistryConfig {

    private String registry = "etcd";
    private String address  = "";
    private String userName;
    private String password;

    //超时时间（毫秒）
    private Long timeOut = 10000L;

}

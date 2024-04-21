package com.wei.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.wei.rpc.constant.RpcConstant;
import com.wei.rpc.constant.SerializerConstatnt;
import lombok.Data;

/**
 * 注册中心注册的服务信息
 */
@Data
public class ServiceMetaInfo {
    private String serviceName;
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    private String serviceHost;
    private Integer servicePort;

    //获取服务键名
    public String getServiceKey(){
        return String.format("%s:%s",serviceName,serviceVersion);
    }

    //获取服务键名
    public String getServiceKeys(){
        return String.format("%s:%s:%s",getServiceKey(),serviceHost,servicePort);
    }

    /**
     * 获取完整服务地址
     */
    public String getServiceAddress(){
        if(!StrUtil.contains(serviceHost,"http")){
            return String.format("http://%s:%s",serviceHost,servicePort);
        }

        return String.format("%s:%s",serviceHost,servicePort);
    }
}

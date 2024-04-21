package com.wei.rpc.model;


import com.wei.rpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    private String serviceName;
    private String methodName;
    private Class<?>[] requestParamTypes;
    private Object[] args;

    private String version = RpcConstant.DEFAULT_SERVICE_VERSION;


    public RpcRequest(String serviceName, String methodName, Class<?>[] requestParamTypes, Object[] args) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.requestParamTypes = requestParamTypes;
        this.args = args;
    }
}

package com.wei.rpc.model;


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
}

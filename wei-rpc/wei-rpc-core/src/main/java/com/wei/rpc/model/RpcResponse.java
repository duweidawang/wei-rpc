package com.wei.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    private Object data;
    private String message;
    private Class<?> dataType;
    private Exception excption;
}

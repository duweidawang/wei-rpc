package com.wei.rpc.bootstrap;

import com.wei.rpc.config.RpcApplication;

public class ConsumerBootstrap {

    public static void init(){
        RpcApplication.getRpcConfig();
    }
}

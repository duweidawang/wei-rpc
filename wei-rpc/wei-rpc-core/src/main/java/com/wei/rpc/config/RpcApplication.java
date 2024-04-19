package com.wei.rpc.config;

import com.wei.rpc.utils.ConfigUtils;

public class RpcApplication {

    private static volatile RpcConfig rpcConfig;
    private static final  String rpcPrefix = "wei-rpc";




    public static void init(){
        try{
            rpcConfig = ConfigUtils.loadConfig(RpcConfig.class,rpcPrefix);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取配置        形如单例双检锁
     */
    public static RpcConfig getRpcConfig(){
        if(rpcConfig == null){
                synchronized (RpcApplication.class){
                    if(rpcConfig ==null ){
                        init();
                    }
                }
        }
        return rpcConfig;
    }



}

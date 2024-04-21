package com.wei.rpc.config;

import com.wei.rpc.registry.Registry;
import com.wei.rpc.registry.RegistryFactory;
import com.wei.rpc.utils.ConfigUtils;

import java.io.IOException;

public class RpcApplication {

    private static volatile RpcConfig rpcConfig;
    private static final  String rpcPrefix = "wei-rpc";

    public static void init(){
        try{
            rpcConfig = ConfigUtils.loadConfig(RpcConfig.class,rpcPrefix);
            init(rpcConfig);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void init(RpcConfig newRpcConfig) throws IOException {
        rpcConfig = newRpcConfig;
            //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        registryConfig = ConfigUtils.loadConfig(RegistryConfig.class, rpcPrefix);
        rpcConfig.setRegistryConfig(registryConfig);
        Registry instance = RegistryFactory.getInstance(registryConfig.getRegistry());
        instance.init(registryConfig);

        //创建并注册Shutdown Hook ,jvm退出时执行操作  //未测试，不确定
        Runtime.getRuntime().addShutdownHook(new Thread(instance::destroy));

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

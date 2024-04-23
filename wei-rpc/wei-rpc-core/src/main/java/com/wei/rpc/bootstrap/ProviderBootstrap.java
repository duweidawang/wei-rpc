package com.wei.rpc.bootstrap;

import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.config.RpcConfig;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.model.ServiceRegisterInfo;
import com.wei.rpc.registry.LocalRegistry;
import com.wei.rpc.registry.Registry;
import com.wei.rpc.registry.RegistryFactory;
import com.wei.rpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {

        //1.根据配置文件得到rpc配置对象
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //2.遍历注册所有服务
        for (ServiceRegisterInfo serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            //获得注册中心        这个注册中心是通过spi读取配置问件进行添加的 和序列化器一样
            Registry instance = RegistryFactory.getInstance(registryConfig.getRegistry());
//            instance.init(registryConfig);

            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            //服务名字就是要远程调用方法所属的类的接口名  要去调用具体实现接口的类的方法
            serviceMetaInfo.setServiceName(serviceName);
            //注册服务所属的域名与端口号，这是要放到注册中心的，让消费者，可以知道向谁构建http请求
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());

            try {
                //调用注册中心的注册方法将serviceMetaInfo注册到注册中心
                instance.register(serviceMetaInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //改为启动tcp服务器
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        }
    }
}

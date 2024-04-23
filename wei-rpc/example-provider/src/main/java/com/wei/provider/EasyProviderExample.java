package com.wei.provider;
import com.wei.common.model.User;
import com.wei.common.service.UserService;
import com.wei.rpc.bootstrap.ProviderBootstrap;
import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.config.RpcConfig;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.model.ServiceRegisterInfo;
import com.wei.rpc.registry.Registry;
import com.wei.rpc.registry.RegistryFactory;
import com.wei.rpc.registry.LocalRegistry;
import com.wei.rpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

public class EasyProviderExample {
    public static void main(String[] args) {
        //要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> objectServiceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(),UserServiceImpl.class);
        serviceRegisterInfoList.add(objectServiceRegisterInfo);
        //进行注册
        ProviderBootstrap.init(serviceRegisterInfoList);

        //将提供的服务提交到本地注册中心
//        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
//        //创建一个web服务器，启动以后监听8080端口，等待请求
//        VertxHttpServer vertxHttpServer = new VertxHttpServer();
//        //在配置中获取端口号
//        SerializerFactory serializerFactory = new SerializerFactory();
//        Map<String, Map<String, Class<?>>> loaderMap = SpiLoader.loaderMap;
//        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}

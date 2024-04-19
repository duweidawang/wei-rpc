package com.wei.provider;

import com.wei.common.service.UserService;
import com.wei.rpc.server.VertxHttpServer;
import com.wei.rpc.registry.LocalRegistry;

public class EasyProviderExample {
    public static void main(String[] args) {
        //将提供的服务提交到本地注册中心
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        //创建一个web服务器，启动以后监听8080端口，等待请求
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8080);

    }
}

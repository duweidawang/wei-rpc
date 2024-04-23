package com.wei.provider;
import com.wei.common.service.UserService;
import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.config.RpcConfig;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.registry.Registry;
import com.wei.rpc.registry.RegistryFactory;
import com.wei.rpc.registry.LocalRegistry;
import com.wei.rpc.server.tcp.VertxTcpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        RpcApplication.init();
        //看出，注册上去的服务名字就是类名
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName,UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        //获得注册中心        这个注册中心是通过spi读取配置问件进行添加的 和序列化器一样
        Registry instance = RegistryFactory.getInstance(registryConfig.getRegistry());
        instance.init(registryConfig);

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        //服务名字就是要远程调用方法所属的类的接口名  要去调用具体实现接口的类的方法
        serviceMetaInfo.setServiceName(serviceName);
        //注册服务所属的域名与端口号，这是要放到注册中心的，让消费者，可以知道像谁构建http请求
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

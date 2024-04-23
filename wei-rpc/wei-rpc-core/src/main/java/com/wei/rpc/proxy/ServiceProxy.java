package com.wei.rpc.proxy;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.config.RpcConfig;
import com.wei.rpc.constant.RpcConstant;
import com.wei.rpc.fault.retry.RetryStrategy;
import com.wei.rpc.fault.retry.RetryStrategyFactory;
import com.wei.rpc.fault.tolerant.TolerantStrategy;
import com.wei.rpc.fault.tolerant.TolerantStrategyFactory;
import com.wei.rpc.loadbalancer.LoadBalancer;
import com.wei.rpc.loadbalancer.LoadBalancerFactory;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.protocol.*;
import com.wei.rpc.registry.Registry;
import com.wei.rpc.registry.RegistryFactory;
import com.wei.rpc.serializer.Serializer;
import com.wei.rpc.serializer.SerializerFactory;
import com.wei.rpc.server.tcp.VertxTcpClient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;


//发起请求是代理对象来发送的，通过代理工厂生成代理对爱，避免了不同的方法调用都需要写一个发起对应的请求的逻辑
public class ServiceProxy implements InvocationHandler {
    //1 获得配置的序列化器对象
    final Serializer serializer = SerializerFactory.getSrializer(RpcApplication.getRpcConfig().getSerializer());
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //2 获得需要调用的方法的类名
        String serviceName = method.getDeclaringClass().getName();
        //3 构建一个rpcRequest对象，就是要调用对应方法信息类名，方法名，参数类型，参数
        RpcRequest rpcRequest = new RpcRequest(serviceName, method.getName(), method.getParameterTypes(), args);

        try {
            // 4对rpcRequest对象进行序列化 的到字节数组
            byte[] serialize = serializer.serialize(rpcRequest);
            //5 获取配置的rpcConfig  rpc配置对象 对象包括名称，版本，rpc服务启动的ip，端口，账号，密码，序列化器，注册中心的配置
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            //6根据上面配置的rpcConfig中的注册中心的配置，加载对应的注册注册中心，实例化一个注册中心对象
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

            //构建服务发现的key，用来从注册中心发现并获取服务。key是由serviceName 加 版本号来构建的（提供者以相同的key存储服务到注册中心）
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            serviceMetaInfo.setServiceName(serviceName);
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfos)){
                throw new RuntimeException("暂无服务地址");
            }
            //todo获得的注册中心的服务 包括serviceName serviceVersion 对应服务的ip 加 端口
//            ServiceMetaInfo serviceMetaInfo1 = serviceMetaInfos.get(0);
            //改为调用负载均衡器获取一个节点
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            HashMap<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName",rpcRequest.getMethodName());
            ServiceMetaInfo select = loadBalancer.select(requestParams, serviceMetaInfos);


            //引入重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            RpcResponse response;
            try {

                 response = retryStrategy.doRetry(() -> {
                    //通过tcp请求客户端发起请求  也是如果出现错误需要重试的代码
                    return VertxTcpClient.doRequest(rpcRequest, select);
                });
            }catch (Exception e){
                TolerantStrategy instance = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                response = instance.doTolerant(null, e);
            }

            return response.getData();


        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

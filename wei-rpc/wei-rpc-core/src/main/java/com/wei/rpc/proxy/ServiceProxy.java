package com.wei.rpc.proxy;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.config.RpcConfig;
import com.wei.rpc.constant.RpcConstant;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.registry.Registry;
import com.wei.rpc.registry.RegistryFactory;
import com.wei.rpc.serializer.Serializer;
import com.wei.rpc.serializer.SerializerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

//代理调用
public class ServiceProxy implements InvocationHandler {

    final Serializer serializer = SerializerFactory.getSrializer(RpcApplication.getRpcConfig().getSerializer());
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Serializer serializer = new JdkSerializer();

        //构建一个请求对象，然后作为body去发送http请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = new RpcRequest(serviceName, method.getName(), method.getParameterTypes(), args);

        try {
            byte[] serialize = serializer.serialize(rpcRequest);
            //从注册中心获取服务提供者 请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            //读取配置文件构建一个Registry对象
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

            //构建服务发现的key
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            serviceMetaInfo.setServiceName(serviceName);
            //调用服务发现发现服务
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

            if(CollUtil.isEmpty(serviceMetaInfos)){
                throw new RuntimeException("暂无服务地址");
            }
            ServiceMetaInfo serviceMetaInfo1 = serviceMetaInfos.get(0);
            //构造并执行请求
            HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo1.getServiceAddress()).body(serialize).execute();
            RpcResponse deserializer = serializer.deserializer(httpResponse.bodyBytes(), RpcResponse.class);
            return deserializer.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

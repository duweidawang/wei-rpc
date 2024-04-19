package com.wei.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.serializer.JdkSerializer;
import com.wei.rpc.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

//代理调用
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = new JdkSerializer();
        //构建一个请求对象，然后作为body去发送http请求
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes(), args);

        try {
            byte[] serialize = serializer.serialize(rpcRequest);
            //构造并执行请求
            HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(serialize).execute();
            RpcResponse deserializer = serializer.deserializer(httpResponse.bodyBytes(), RpcResponse.class);
            return deserializer.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

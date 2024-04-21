package com.wei.rpc.server;

import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.registry.LocalRegistry;
import com.wei.rpc.serializer.JdkSerializer;
import com.wei.rpc.serializer.Serializer;
import com.wei.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//rpc请求处理器
public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
//        final Serializer serializer = new JdkSerializer();
        //读取配置中指定的序列化器
        final Serializer serializer = SerializerFactory.getSrializer(RpcApplication.getRpcConfig().getSerializer());
        httpServerRequest.bodyHandler(body ->{
            byte[] aByte = body.getBytes();
            RpcRequest rpcRequest = null;

            try {
                rpcRequest = serializer.deserializer(aByte,RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            RpcResponse response = new RpcResponse();
            if(rpcRequest == null){
                response.setMessage("body data is null");
                doResponse(httpServerRequest,response,serializer);
                return;
            }

            try {
            //如果rpcRequest不为null，即请求有具体数据，需要分发调用
            //获得请求方法
            String methodName = rpcRequest.getMethodName();

            Class<?> aClass = LocalRegistry.get(rpcRequest.getServiceName());

            Method method = aClass.getMethod(methodName,rpcRequest.getRequestParamTypes());
            //反射调用具体方法后获得结果
            Object invoke = method.invoke(aClass.newInstance(), rpcRequest.getArgs());
            //得到结果后，封装返回结果
            response.setData(invoke);
            response.setDataType(method.getReturnType());
            response.setMessage("ok");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            //然后响应
            doResponse(httpServerRequest,response,serializer);
        });
    }

//进行响应
    void doResponse(HttpServerRequest request,RpcResponse rpcResponse,Serializer serializer){
        HttpServerResponse response = request.response();
        response.putHeader("content-type","application/json");
        try {
            byte[] serialize = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(serialize));
        } catch (IOException e) {
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }

}

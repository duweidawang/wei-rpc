package com.wei.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂    用与创建代理对象
 */
public class ServiceProxyFactory {

    public static <T> T getProxy(Class<T> serviceClass){
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),new Class[]{serviceClass},new ServiceProxy());
    }


}

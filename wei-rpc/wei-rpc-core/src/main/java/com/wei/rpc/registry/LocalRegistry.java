package com.wei.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟注册中心
 */
public class LocalRegistry {

    //存储信息
    private static final Map<String,Class<?>> map = new ConcurrentHashMap<>();

    //注册服务
    public static void register(String name , Class<?> clazz){
        map.put(name,clazz);
    }
    //获取服务
    public static Class<?> get(String serviceName){
        return map.get(serviceName);
    }
    //删除服务
    public static void remove(String serviceName){
        map.remove(serviceName);
    }

}

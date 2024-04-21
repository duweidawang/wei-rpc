package com.wei.rpc.serializer;

import com.wei.rpc.constant.SerializerConstatnt;
import com.wei.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂  创建序列化器 并用于获取序列化器
 */
public class SerializerFactory {

//    //序列化器映射
//    private static final Map<String,Serializer> SERIALIZER_MAP = new HashMap(){{
//        put(SerializerConstatnt.JDK,new JdkSerializer());
//        put(SerializerConstatnt.JSON,new JsonSerializer());
//        put(SerializerConstatnt.HESSION,new HessionSerializer());
//    }};
    static {
    SpiLoader.load(Serializer.class);
}


    //默认序列化器
    private static final Serializer DEFAULT_SERILIZER = new JdkSerializer();

    //获取
    public static Serializer getSrializer(String key){
//        return SERIALIZER_MAP.getOrDefault(key,DEFAULT_SERILIZER);
        return SpiLoader.getInstance(Serializer.class,key);
    }
}

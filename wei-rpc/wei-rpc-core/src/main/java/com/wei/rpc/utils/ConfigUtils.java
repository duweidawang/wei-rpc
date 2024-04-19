package com.wei.rpc.utils;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import java.io.IOException;

/**
 * 加载rpc配置为对象
 */
public class ConfigUtils {

    public static <T> T loadConfig(Class<T> tClass,String prefix) throws IOException {
        return loadConfig(tClass,prefix,"");

    }


    public static <T> T loadConfig(Class<T> tClass,String prefix,String environment){
        StringBuilder application = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)){
            application.append("-").append(environment);

        }
        application.append(".properties");
        Props props = new Props(application.toString());
        return props.toBean(tClass,prefix);

    }



}

package com.wei.rpc.cache;

import com.wei.rpc.model.ServiceMetaInfo;
import lombok.Data;

import java.util.List;

@Data
public class RegistryServiceCache {

    //服务缓存
    List<ServiceMetaInfo> serviceCache;

    //写缓存
    public void writeCache(List<ServiceMetaInfo> newServiceCache){
        serviceCache  = newServiceCache;
    }

    //读缓存
   public  List<ServiceMetaInfo> readCache(){
        return serviceCache;
    }

    //删除缓存
    public void deleteCache(){
        serviceCache.clear();
    }

}

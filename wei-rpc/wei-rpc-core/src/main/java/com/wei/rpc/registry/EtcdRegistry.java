package com.wei.rpc.registry;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.wei.rpc.cache.RegistryServiceCache;
import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private Client client;
    private KV kvClient;
    //存放当前服务注册的服务
    private final Set<String> localRegistryNodeKeySet = new HashSet();

    //服务的本地缓存
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    //存放正在监听的key集合
    private final Set<String> watchKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    //测试etcd是否可用
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        io.etcd.jetcd.Client build = io.etcd.jetcd.Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = build.getKVClient();
        ByteSequence from = ByteSequence.from("test_key".getBytes());
        ByteSequence from1 = ByteSequence.from("test_value".getBytes());
        PutResponse putResponse = kvClient.put(from, from1).get();
        System.out.println(putResponse);

        CompletableFuture<GetResponse> getResponseCompletableFuture = kvClient.get(from);
        GetResponse getResponse = getResponseCompletableFuture.get();
        System.out.println(getResponse);
        kvClient.delete(from).get();
    }




    @Override
    public void init(RegistryConfig registryConfig) {
        //这个时间表示客户端在连接 etcd 服务器时等待的最长时间。如果在此时间内无法建立连接，将会抛出连接超时的异常
        //getAddress 表名注册中心的地址，为了建立连接
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis((registryConfig.getTimeOut()))).build();
        kvClient = client.getKVClient();
//        heartBeat();


    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //创建lease和kv客户端
        Lease leaseClient = client.getLeaseClient();
        //创建一个30秒的租约
        long id = leaseClient.grant(300).get().getID();
        //设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceKey();

        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(id).build();
        kvClient.put(key,value,putOption).get();

        //添加节点信息到本地缓存
        localRegistryNodeKeySet.add(registerKey);

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH+serviceMetaInfo.getServiceKey(),StandardCharsets.UTF_8)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceKey();
        //服务删除也要删除本地的节点信息
        localRegistryNodeKeySet.remove(registerKey);

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //优先从缓存中获取，没有就添加
        List<ServiceMetaInfo> serviceCache = registryServiceCache.readCache();
        if(!CollUtil.isEmpty(serviceCache)){
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee");
            return serviceCache;
        }

        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey;
        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听 key 的变化  如果是删除key的话，需要将本地缓存中的服务删除
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            //返回结果

            //如果上面缓存没有返回，就在这里写入缓存
            registryServiceCache.writeCache(serviceMetaInfoList);

            return serviceMetaInfoList;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void heartBeat() {
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : localRegistryNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient  = client.getWatchClient();
        //之前未被监听，开启监听
        boolean add = watchKeySet.add(serviceNodeKey);
        if(add){
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8),response ->{
                for (WatchEvent event :response.getEvents()){
                    switch (event.getEventType()){
                        case DELETE:
                            //key 删除时触发
                            registryServiceCache.deleteCache();
                            System.out.println(111);
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            } );
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        //主动下线，如果节点主动下线，那么也需要主动删除注册中心的服务
        for (String key:localRegistryNodeKeySet
             ) {
            ByteSequence from = ByteSequence.from(key, StandardCharsets.UTF_8);
            try {
                kvClient.delete(from).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (kvClient != null){
            kvClient.close();
        }
        if (client != null){
            client.close();
        }
    }
}

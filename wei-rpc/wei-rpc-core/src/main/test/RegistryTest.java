import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.IdUtil;
import cn.hutool.cron.CronUtil;
import com.github.rholder.retry.RetryException;
import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.fault.retry.FixedIntervalRetryStrategy;
import com.wei.rpc.fault.retry.NoRetryStrategy;
import com.wei.rpc.fault.retry.RetryStrategy;
import com.wei.rpc.loadbalancer.*;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.protocol.*;
import com.wei.rpc.registry.EtcdRegistry;
import com.wei.rpc.registry.Registry;

import com.wei.rpc.serializer.HessionSerializer;
import com.wei.rpc.serializer.JdkSerializer;
import com.wei.rpc.serializer.JsonSerializer;
import com.wei.rpc.serializer.KryoSerializer;
import io.vertx.core.buffer.Buffer;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegistryTest {


    final Registry registry  = new EtcdRegistry();


    @Before
    public void init(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void refister() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myservice");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);

        registry.register(serviceMetaInfo);
        List<ServiceMetaInfo> serviceMetaInfos1 = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        System.out.println(serviceMetaInfos1);

        Thread.sleep(1000*60);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        System.out.println(serviceMetaInfos);

    }

    @Test
    public void refister1() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myservice");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);

        registry.register(serviceMetaInfo);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        System.out.println(serviceMetaInfos);
        registry.unRegister(serviceMetaInfo);

        List<ServiceMetaInfo> serviceMetaInfos1 = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        System.out.println(serviceMetaInfos1);




    }
    @Test
    public void serviceDiscovery(){
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myservice");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        System.out.println(serviceMetaInfos);
    }

    @Test
    public void testDeleteRemoteService_whenQuieService(){

    }

    @Test
    public void testMessageEncoderWithDecoder() throws IOException {
        ProtocolMessage<RpcRequest> objectProtocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte)ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(19);

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("myservice");
        rpcRequest.setVersion("0.1");
        rpcRequest.setMethodName("getUser");
        rpcRequest.setRequestParamTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"aaa","bbb"});

        objectProtocolMessage.setBody(rpcRequest);
        objectProtocolMessage.setHeader(header);


        System.out.println(objectProtocolMessage);
        Buffer encode = ProtocolMessageEncoder.encode(objectProtocolMessage);
        System.out.println(encode);
        ProtocolMessage<?> decode = ProtocolMessageDecoder.decode(encode);
        System.out.println(decode);
    }

    private  LoadBalancer loadBalancer= new ConsistentHashLoadBalancer();
    @Test
    public void testLoadBanlancerSelect(){
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("methodName","qwerzxv");

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myservice");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);

        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceName("myservice");
        serviceMetaInfo1.setServiceVersion("1.0");
        serviceMetaInfo1.setServiceHost("daidu.com");
        serviceMetaInfo1.setServicePort(1234);

        List<ServiceMetaInfo> serviceMetaInfos = Arrays.asList(serviceMetaInfo, serviceMetaInfo1);
        for(int i=0;i<3;i++){
            ServiceMetaInfo select = loadBalancer.select(objectObjectHashMap, serviceMetaInfos);
            System.out.println(select.getServiceHost());
        }

    }

    @Test
    public void testRetryStrategy() throws Exception {
        RetryStrategy retryStrategy= new NoRetryStrategy();
        RpcResponse response =retryStrategy.doRetry(() -> {
            System.out.println("测试重试");
            throw new Exception("模拟重试");
        });
        System.out.println(response);
    }


    @Test
    public void testSerializer() throws IOException {
        JsonSerializer jsonSerializer = new JsonSerializer();
        KryoSerializer kryoSerializer = new KryoSerializer();
        JdkSerializer jdkSerializer = new JdkSerializer();
        HessionSerializer hessionSerializer = new HessionSerializer();

        String name ="1111111111111111sdfafsewqrqsadfqwerasdfqwer";
        byte[] serialize = jsonSerializer.serialize(name);
        byte[] serialize1 = kryoSerializer.serialize(name);
        byte[] serialize2 = jdkSerializer.serialize(name);
        byte[] serialize3 = hessionSerializer.serialize(name);
        System.out.println("json length= "+serialize.length);
        System.out.println("kryo length = "+serialize1.length);
        System.out.println("jdk length = "+serialize2.length);
        System.out.println("hession length = "+serialize3.length);

        long l = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            byte[] serialize4 = jsonSerializer.serialize(name);
            jsonSerializer.deserializer(serialize4,String.class);
        }
        long l1 = System.currentTimeMillis();
        System.out.println("json "+(l1-l));

        long l2 = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            byte[] serialize4 = kryoSerializer.serialize(name);
            kryoSerializer.deserializer(serialize4,String.class);
        }
        long l3 = System.currentTimeMillis();
        System.out.println("kryo "+(l3-l2));

        long l4 = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            byte[] serialize4 = jdkSerializer.serialize(name);
            jdkSerializer.deserializer(serialize4,String.class);
        }
        long l5 = System.currentTimeMillis();
        System.out.println("jdk "+(l5-l4));

        long l6 = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            byte[] serialize4 = hessionSerializer.serialize(name);
            hessionSerializer.deserializer(serialize4,String.class);
        }
        long l7 = System.currentTimeMillis();
        System.out.println("hession "+(l7-l6));

    }


}

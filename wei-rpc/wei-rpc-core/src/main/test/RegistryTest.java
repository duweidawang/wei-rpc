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


}

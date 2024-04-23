import cn.hutool.core.util.IdUtil;
import cn.hutool.cron.CronUtil;
import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.protocol.*;
import com.wei.rpc.registry.EtcdRegistry;
import com.wei.rpc.registry.Registry;

import io.vertx.core.buffer.Buffer;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.util.List;

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


}

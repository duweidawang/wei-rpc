import cn.hutool.cron.CronUtil;
import com.wei.rpc.config.RegistryConfig;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.registry.EtcdRegistry;
import com.wei.rpc.registry.Registry;
import org.junit.Before;
import org.junit.Test;

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
    public void s(){
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

}

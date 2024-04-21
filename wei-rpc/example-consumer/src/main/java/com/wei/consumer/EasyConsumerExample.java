package com.wei.consumer;

import com.wei.common.model.User;
import com.wei.common.service.UserService;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.config.RpcConfig;
import com.wei.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        for(int i =0;i<4;i++) {

            UserService userService = ServiceProxyFactory.getProxy(UserService.class);
            User user = new User();
            user.setName("wei");
            User user1 = userService.getUser(user);
            if (user1 != null) {
                System.out.println(user1.getName());
            } else {
                System.out.println("user1 == null");
            }
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            System.out.println(rpcConfig);
            if(i==1){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

    }
}

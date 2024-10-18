package com.wei.controller;

import com.wei.common.model.User;
import com.wei.common.service.UserService;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.config.RpcConfig;
import com.wei.rpc.proxy.ServiceProxyFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @GetMapping("/getHttp")
    public String getHttp(){
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("wei");
        User user1 = userService.getUser(user);
        if (user1 != null) {
            System.out.println(user1.getName());
        } else {
            System.out.println("user1 == null");
        }
       return user1.toString();

    }

}

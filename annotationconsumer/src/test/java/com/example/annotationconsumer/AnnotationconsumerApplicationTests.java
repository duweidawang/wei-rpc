package com.example.annotationconsumer;

import com.wei.common.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AnnotationconsumerApplicationTests {

    @Resource
    UserServiceImpl controllerr;

    @Test
    void contextLoads() {
        User user  = controllerr.test();
        System.out.println(user.getName());
    }

}

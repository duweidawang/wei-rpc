package com.example.annotationconsumer;

import com.duwei.durpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class AnnotationconsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationconsumerApplication.class, args);
    }

}

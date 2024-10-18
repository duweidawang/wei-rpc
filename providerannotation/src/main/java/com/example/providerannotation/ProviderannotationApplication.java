package com.example.providerannotation;

import com.duwei.durpcspringbootstarter.annotation.EnableRpc;
import com.duwei.durpcspringbootstarter.annotation.RpcReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class ProviderannotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderannotationApplication.class, args);
    }

}

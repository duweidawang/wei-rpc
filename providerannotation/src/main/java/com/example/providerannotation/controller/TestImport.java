package com.example.providerannotation.controller;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@Configuration
@Import(MyImportSelector.class)
public class TestImport {

    @Bean
    public Object per1(){
        return new Object();
    }

    public static void main(String[] args) {

        AnnotationConfigApplicationContext  context = new AnnotationConfigApplicationContext(TestImport.class);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

}

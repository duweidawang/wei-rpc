package com.example.providerannotation.controller;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        String beanName =  "com/example/providerannotation/controller/TestImport3";
        String beanName1 =  "com/example/providerannotation/controller/TestImport4";
        String beanName2 ="com/example/providerannotation/controller/TestImport5";
        String[] strings = {beanName, beanName1, beanName2};
        return strings;
    }
}

package com.ksh.proxy;

import java.util.Locale;

public class HelloUppercase implements Hello{
    // 위임할 타겟 오브젝트
    Hello hello;

    public HelloUppercase(Hello hello){
        this.hello = hello;
    }

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}

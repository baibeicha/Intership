package com.intership.orderservice;

import org.springframework.boot.SpringApplication;

public class TestIntershipOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(IntershipOrderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

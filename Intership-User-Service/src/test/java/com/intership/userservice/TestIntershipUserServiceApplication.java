package com.intership.userservice;

import org.springframework.boot.SpringApplication;

public class TestIntershipUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(IntershipUserServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

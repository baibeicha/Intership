package com.intership.paymentservice;

import org.springframework.boot.SpringApplication;

public class TestIntershipPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(IntershipPaymentServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

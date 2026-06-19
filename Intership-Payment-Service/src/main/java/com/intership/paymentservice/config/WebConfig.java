package com.intership.paymentservice.config;

import com.intership.paymentservice.service.RandomNumberService;
import com.intership.paymentservice.service.impl.RandomNumberApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Value("${random.api.url}")
    private String apiUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RandomNumberService paymentStatusService(RestTemplate restTemplate) {
        return new RandomNumberApiService(restTemplate, apiUrl);
    }
}
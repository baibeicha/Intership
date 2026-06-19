package com.intership.apigateway.config;

import com.intership.apigateway.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${service.auth}")
    private String authServiceUrl;

    @Value("${service.user}")
    private String userServiceUrl;

    @Value("${service.order}")
    private String orderServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(WebClient webClient) {
        return new JwtAuthFilter(webClient, authServiceUrl);
    }
}
package com.intership.userservice.configuration;

import com.intership.userservice.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${auth.url}")
    private String authUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(RestTemplate restTemplate) {
        return new JwtAuthFilter(restTemplate, authUrl);
    }
}

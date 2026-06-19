package com.intership.authenticationservice.config;

import com.intership.authenticationservice.service.TokensService;
import com.intership.authenticationservice.service.UserService;
import com.intership.authenticationservice.service.impl.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Value("${service.user}")
    private String userServiceUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserService userService(RestTemplate restTemplate, TokensService tokensService) {
        return new UserServiceClient(restTemplate, userServiceUrl, tokensService);
    }
}

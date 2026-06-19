package com.intership.orderservice.config;

import com.intership.orderservice.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@Configuration
public class WebConfig {

    @Value("${service.auth}")
    private String authServiceUrl;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(authorizationInterceptor());
        return restTemplate;
    }

    private ClientHttpRequestInterceptor authorizationInterceptor() {
        return (request, body, execution) -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String auth = attrs.getRequest().getHeader("Authorization");
                if (auth != null && !auth.isBlank()) {
                    request.getHeaders().setBearerAuth(auth.substring(7));
                }
            }
            return execution.execute(request, body);
        };
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(RestTemplate restTemplate) {
        return new JwtAuthFilter(restTemplate, authServiceUrl);
    }
}

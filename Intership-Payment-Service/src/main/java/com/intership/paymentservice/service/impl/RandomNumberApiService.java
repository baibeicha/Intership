package com.intership.paymentservice.service.impl;

import com.intership.paymentservice.service.RandomNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class RandomNumberApiService implements RandomNumberService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Override
    public int getRandomNumber() {
        try {
            String response = restTemplate.getForObject(baseUrl, String.class);
            return Integer.parseInt(response.trim());
        } catch (Exception e) {
            return 1;
        }
    }
}

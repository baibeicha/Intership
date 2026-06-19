package com.intership.orderservice.service.impl;

import com.intership.orderservice.exception.UserNotFoundException;
import com.intership.orderservice.exception.UserServiceException;
import com.intership.orderservice.model.dto.UserResponse;
import com.intership.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceClient implements UserService {

    @Value("${service.user}")
    private String userServiceUrl;

    private final RestTemplate restTemplate;

    @Override
    public UserResponse getUserByEmail(String email) {
        try {
            UserResponse response = restTemplate.getForObject(
                    userServiceUrl + "/api/v1/users/email/" + email, UserResponse.class);
            if (response == null) {
                throw new UserServiceException("User service returned null for email: " + email);
            }
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User not found with email: " + email, e);
        } catch (Exception e) {
            throw new UserServiceException("Failed to fetch user information for user with email: " + email, e);
        }
    }

    @Override
    public Map<String, UserResponse> getUsersByEmails(List<String> emails) {
        Map<String, UserResponse> users = new HashMap<>();
        emails.stream().distinct()
                .forEach(email -> users.put(email, getUserByEmail(email)));
        return users;
    }
}

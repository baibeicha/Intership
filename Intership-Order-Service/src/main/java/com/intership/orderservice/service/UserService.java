package com.intership.orderservice.service;

import com.intership.orderservice.model.dto.UserResponse;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserResponse getUserByEmail(String email);

    Map<String, UserResponse> getUsersByEmails(List<String> emails);
}

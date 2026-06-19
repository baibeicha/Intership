package com.intership.authenticationservice.service;

import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.UserDto;
import com.intership.authenticationservice.model.dto.UserRegistrationRequest;
import com.intership.authenticationservice.model.entity.UserCredentials;

public interface UserCredentialsService {
    boolean existsByUsername(String username);
    UserCredentials getByUsername(String username);
    AuthEntity findByUsername(String username);
    AuthEntity save(AuthEntity authEntity);

    void deleteByUsername(String username);

    UserDto register(UserRegistrationRequest request);
}

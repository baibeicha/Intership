package com.intership.authenticationservice.service;

import com.intership.authenticationservice.exception.UserRegistrationException;
import com.intership.authenticationservice.model.dto.UserDto;
import com.intership.authenticationservice.model.dto.UserRegistrationRequest;

public interface UserService {
    UserDto registerUser(UserRegistrationRequest request) throws UserRegistrationException;
}

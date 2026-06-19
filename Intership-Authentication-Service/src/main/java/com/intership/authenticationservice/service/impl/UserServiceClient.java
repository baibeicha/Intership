package com.intership.authenticationservice.service.impl;

import com.intership.authenticationservice.exception.UserRegistrationException;
import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.Tokens;
import com.intership.authenticationservice.model.dto.UserDto;
import com.intership.authenticationservice.model.dto.UserRegistrationRequest;
import com.intership.authenticationservice.service.TokensService;
import com.intership.authenticationservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class UserServiceClient implements UserService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final TokensService tokensService;

    @Transactional
    @Override
    public UserDto registerUser(UserRegistrationRequest request) throws UserRegistrationException {
        try {
            Tokens tokens = tokensService.generateTokens(new AuthEntity(request.getUsername(), request.getPassword()));
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.add("Authorization", "Bearer " + tokens.getAccessToken());

            HttpEntity<?> entity = new HttpEntity<>(buildUser(request), authHeaders);

            UserDto result = restTemplate.postForObject(userServiceUrl, entity, UserDto.class);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserRegistrationException(e);
        }
    }

    private UserDto buildUser(UserRegistrationRequest request) {
        return UserDto.builder()
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .name(request.getUsername())
                .surname(request.getSurname())
                .build();
    }
}

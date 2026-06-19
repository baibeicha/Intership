package com.intership.authenticationservice.service.impl;

import com.intership.authenticationservice.exception.UserCredentialsNotFoundException;
import com.intership.authenticationservice.exception.UserRegistrationException;
import com.intership.authenticationservice.mapper.UserCredentialsMapper;
import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.UserDto;
import com.intership.authenticationservice.model.dto.UserRegistrationRequest;
import com.intership.authenticationservice.model.entity.UserCredentials;
import com.intership.authenticationservice.repository.UserCredentialsRepository;
import com.intership.authenticationservice.service.UserCredentialsService;
import com.intership.authenticationservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCredentialsMapper userCredentialsMapper;
    private final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public boolean existsByUsername(String username) {
        return userCredentialsRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public UserCredentials getByUsername(String username) {
        return userCredentialsRepository.findByUsername(username)
                .orElseThrow(() -> new UserCredentialsNotFoundException(username));
    }

    @Transactional(readOnly = true)
    @Override
    public AuthEntity findByUsername(String username) {
        return userCredentialsMapper.toDto(getByUsername(username));
    }

    @Transactional
    @Override
    public AuthEntity save(AuthEntity authEntity) {
        authEntity.setPassword(passwordEncoder.encode(authEntity.getPassword()));

        UserCredentials userCredentials =
                userCredentialsRepository.findByUsername(authEntity.getUsername()).orElse(null);
        if (userCredentials == null) {
            userCredentials = userCredentialsMapper.toEntity(authEntity);
            userCredentials.setUserId(0L);
        } else {
            userCredentials = userCredentialsMapper.merge(userCredentials, authEntity);
        }

        userCredentials = userCredentialsRepository.save(userCredentials);
        return userCredentialsMapper.toDto(userCredentials);
    }

    @Transactional
    @Override
    public void deleteByUsername(String username) {
        userCredentialsRepository.deleteByUsername(username);
    }

    @Transactional
    @Override
    public UserDto register(UserRegistrationRequest request) {
        AuthEntity authEntity = save(new AuthEntity(request.getUsername(), request.getPassword()));

        try {
            UserDto user = userService.registerUser(request);

            UserCredentials userCredentials = getByUsername(authEntity.getUsername());
            userCredentials.setUserId(user.getId());
            userCredentialsRepository.save(userCredentials);

            return user;
        } catch (UserRegistrationException e) {
            deleteByUsername(authEntity.getUsername());
            throw e;
        }
    }
}

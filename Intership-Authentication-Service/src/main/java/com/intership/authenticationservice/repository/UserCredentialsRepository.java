package com.intership.authenticationservice.repository;

import com.intership.authenticationservice.model.entity.UserCredentials;

import java.util.Optional;

public interface UserCredentialsRepository {
    Optional<UserCredentials> findByUsername(String username);
    UserCredentials save(UserCredentials userCredentials);

    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}

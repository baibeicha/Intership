package com.intership.authenticationservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentials {
    private Long id;
    private Long userId;
    private String username;
    private String password;
}

package com.intership.authenticationservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tokens {

    @NotNull
    private String accessToken;

    @NotNull
    private String refreshToken;
}

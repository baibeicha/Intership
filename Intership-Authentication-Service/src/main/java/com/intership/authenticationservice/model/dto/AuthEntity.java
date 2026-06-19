package com.intership.authenticationservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class AuthEntity {

    @NotNull
    @Length(min = 3, max = 255)
    private String username;

    @NotNull
    private String password;
}

package com.intership.userservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {

    private Long id;

    @NotNull
    @Length(min = 3, max = 50)
    private String name;

    @NotNull
    @Length(min = 3, max = 50)
    private String surname;

    private LocalDate birthDate;

    @NotNull
    @Email
    private String email;
    
    private List<CardDto> cards;
}
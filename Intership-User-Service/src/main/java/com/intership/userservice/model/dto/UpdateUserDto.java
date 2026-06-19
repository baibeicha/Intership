package com.intership.userservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
public class UpdateUserDto {

    @NotNull
    @Length(min = 3, max = 50)
    private String name;

    @NotNull
    @Length(min = 3, max = 50)
    private String surname;

    private LocalDate birthDate;
}

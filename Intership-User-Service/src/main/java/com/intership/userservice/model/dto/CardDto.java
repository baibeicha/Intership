package com.intership.userservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CardDto {

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Length(min = 19, max = 19)
    private String number;

    @NotNull
    @Length(max = 100)
    private String holder;

    @Length(min = 7, max = 7)
    private String expirationDate;
}

package com.douradelivery.after.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateResquestXXXXXXXXXXX(

        @Schema(example = "João Pamonha")
        @NotBlank
        String name
        ) {}

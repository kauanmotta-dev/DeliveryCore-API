package com.douradelivery.after.model.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdatePasswordRequestDTO(

        @NotBlank
        String currentPassword,

        @NotBlank
        String newPassword
        ) {}

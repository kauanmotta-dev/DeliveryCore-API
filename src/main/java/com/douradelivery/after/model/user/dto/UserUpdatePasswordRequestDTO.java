package com.douradelivery.after.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserUpdatePasswordRequestDTO(

        @Schema(example = "SENHAatual123")
        @NotBlank
        String currentPassword,
        @Schema(example = "SENHAnova123")
        @NotBlank
        String newPassword
        ) {}

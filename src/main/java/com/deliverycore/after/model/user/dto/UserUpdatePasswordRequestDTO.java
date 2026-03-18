package com.deliverycore.after.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordRequestDTO(

        @Schema(example = "SENHAatual123")
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @Schema(example = "SENHAnova123")
        @NotBlank
        @Size(min = 6, message = "New password must have at least 6 characters")
        String newPassword
        ) {}

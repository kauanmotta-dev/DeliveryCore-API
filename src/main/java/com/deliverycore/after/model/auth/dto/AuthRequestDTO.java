package com.deliverycore.after.model.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(

    @Schema(example = "joao@email.com")
    @Email
    @NotBlank
    String email,

    @Schema(example = "123456")
    @NotBlank
    String password
) {}

package com.douradelivery.after.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequestDTO(

        @Schema(example = "João Silva")
        @NotBlank
        String name,

        @Schema(example = "joao@email.com")
        @Email
        @NotBlank
        String email,

        @Schema(example = "12345678901")
        @NotBlank
        @Size(min = 11, max = 11)
        String cpf,

        @Schema(example = "123456@Aa")
        @NotBlank
        String password
) {}


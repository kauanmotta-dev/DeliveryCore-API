package com.deliverycore.after.model.user.dto;

import com.deliverycore.after.model.user.enums.UserRole;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String cpf,
        UserRole role
) {}

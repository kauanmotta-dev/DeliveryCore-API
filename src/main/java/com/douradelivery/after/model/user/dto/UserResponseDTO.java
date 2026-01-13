package com.douradelivery.after.model.user.dto;

import com.douradelivery.after.model.user.entity.UserRole;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String cpf,
        UserRole role
) {}

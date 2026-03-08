package com.douradelivery.after.model.deliverymanVerification.dto;

import com.douradelivery.after.model.deliverymanVerification.enums.VerificationStatus;

import java.time.LocalDateTime;

public record DeliverymanVerificationResponseDTO(

        Long id,
        Long userId,
        String userName,
        VerificationStatus status,

        String vehicleType,
        String vehiclePlate,

        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {}
package com.douradelivery.after.model.deliverymanVerification.dto;

import com.douradelivery.after.model.deliverymanVerification.enums.VerificationStatus;

public record DeliverymanVerificationEventDTO(
        VerificationStatus status
) {}

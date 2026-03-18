package com.deliverycore.after.model.deliverymanVerification.dto;

import com.deliverycore.after.model.deliverymanVerification.enums.VerificationStatus;

public record DeliverymanVerificationEventDTO(
        VerificationStatus status
) {}

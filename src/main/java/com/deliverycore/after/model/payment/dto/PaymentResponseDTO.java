package com.deliverycore.after.model.payment.dto;

import com.deliverycore.after.model.payment.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponseDTO(
        Long paymentId,
        PaymentStatus status,
        BigDecimal amount
) {}

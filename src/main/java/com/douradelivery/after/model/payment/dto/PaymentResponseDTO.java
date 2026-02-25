package com.douradelivery.after.model.payment.dto;

import com.douradelivery.after.model.payment.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponseDTO(
        Long paymentId,
        PaymentStatus status,
        BigDecimal amount
) {}

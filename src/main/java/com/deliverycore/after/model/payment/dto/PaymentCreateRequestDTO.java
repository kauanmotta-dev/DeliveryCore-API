package com.deliverycore.after.model.payment.dto;

import com.deliverycore.after.model.payment.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentCreateRequestDTO(
        BigDecimal amount,
        PaymentMethod paymentMethod
) {}


package com.douradelivery.after.model.payment.dto;

import com.douradelivery.after.model.payment.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentCreateRequestDTO(
        BigDecimal amount,
        PaymentMethod paymentMethod
) {}


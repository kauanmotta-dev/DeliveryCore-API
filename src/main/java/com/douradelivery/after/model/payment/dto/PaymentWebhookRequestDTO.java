package com.douradelivery.after.model.payment.dto;

import java.math.BigDecimal;

public record PaymentWebhookRequestDTO(
        String externalPaymentId,
        Long paymentId,
        BigDecimal amount,
        String status
) {}

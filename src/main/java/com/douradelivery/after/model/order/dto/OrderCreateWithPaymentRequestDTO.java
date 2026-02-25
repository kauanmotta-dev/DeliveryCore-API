package com.douradelivery.after.model.order.dto;

import com.douradelivery.after.model.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderCreateWithPaymentRequestDTO(

        @NotBlank
        String originAddress,

        @NotBlank
        String destinationAddress,

        @NotNull
        BigDecimal amount,

        @NotNull
        PaymentMethod paymentMethod
) {}
package com.deliverycore.after.model.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    PIX(true),
    CREDIT_CARD(false),
    DEBIT_CARD(false),
    WALLET(false);

    private final boolean enabled;

}


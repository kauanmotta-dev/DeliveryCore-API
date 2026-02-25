package com.douradelivery.after.model.payment.enums;

public enum PaymentMethod {

    PIX(true),
    CREDIT_CARD(false),
    DEBIT_CARD(false),
    WALLET(false);

    private final boolean enabled;

    PaymentMethod(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}


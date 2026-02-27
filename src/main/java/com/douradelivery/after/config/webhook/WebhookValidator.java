package com.douradelivery.after.config.webhook;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.order.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.douradelivery.after.model.order.enums.OrderStatus.CANCELED;
import static com.douradelivery.after.model.order.enums.OrderStatus.REFUNDED;

@Component
public class WebhookValidator {

    @Value("${payment.webhook.secret}")
    private String secret;

    public void validate(String receivedSignature) {
        if (!secret.equals(receivedSignature)) {
            throw new BusinessException("Invalid webhook signature");
        }
    }
}


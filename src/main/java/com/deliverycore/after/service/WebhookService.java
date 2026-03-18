package com.deliverycore.after.service;

import com.deliverycore.after.config.webhook.WebhookValidator;
import com.deliverycore.after.exception.exceptions.BusinessException;
import com.deliverycore.after.model.payment.dto.PaymentWebhookRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookValidator webhookValidator;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public void processPixWebhook(String rawPayload, String signature) {

        webhookValidator.validate(rawPayload, signature);

        try {

            PaymentWebhookRequestDTO event =
                    objectMapper.readValue(rawPayload, PaymentWebhookRequestDTO.class);

            Long orderId = paymentService.handlePixWebhook(event);

            if (orderId != null) {
                orderService.markAsPaid(orderId);
            }

        } catch (Exception e) {
            throw new BusinessException("Invalid webhook payload");
        }
    }
}

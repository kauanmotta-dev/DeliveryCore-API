package com.douradelivery.after.controller;

import com.douradelivery.after.config.webhook.WebhookValidator;
import com.douradelivery.after.model.payment.dto.PaymentWebhookRequestDTO;
import com.douradelivery.after.service.OrderService;
import com.douradelivery.after.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/webhooks/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final WebhookValidator webhookValidator;

    @PostMapping("/pix")
    public ResponseEntity<Void> handlePix(
            @RequestBody PaymentWebhookRequestDTO event,
            @RequestHeader("X-Signature") String signature
    ) {

        webhookValidator.validate(event.toString(), signature);

        Long orderId = paymentService.handlePixWebhook(event);

        if (orderId != null) {
            orderService.markAsPaid(orderId);
        }

        return ResponseEntity.ok().build();
    }
}

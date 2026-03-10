package com.douradelivery.after.controller;

import com.douradelivery.after.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final WebhookService webhookService;

    @PostMapping("/pix")
    public ResponseEntity<Void> handlePix(
            @RequestBody String rawPayload,
            @RequestHeader("X-Signature") String signature
    ) {

        webhookService.processPixWebhook(rawPayload, signature);

        return ResponseEntity.ok().build();
    }
}
package com.douradelivery.after.controller;

import com.douradelivery.after.model.payment.dto.PaymentWebhookRequestDTO;
import com.douradelivery.after.service.OrderService;
import com.douradelivery.after.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/webhooks/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping("/pix")
    public ResponseEntity<Void> handlePix(@RequestBody PaymentWebhookRequestDTO event) {

        Long orderId = paymentService.handlePixWebhook(event);

        if (orderId != null) {
            orderService.markAsPaid(orderId);
        }

        return ResponseEntity.ok().build();
    }
}

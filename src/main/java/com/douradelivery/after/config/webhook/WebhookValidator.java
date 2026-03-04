package com.douradelivery.after.config.webhook;

import com.douradelivery.after.exception.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class WebhookValidator {

    @Value("${payment.webhook.secret}")
    private String secret;

    public void validate(String payload, String receivedSignature) {

        try {

            String expectedSignature = generateHmac(payload, secret);

            if (!expectedSignature.equals(receivedSignature)) {
                throw new BusinessException("Invalid webhook signature");
            }

        } catch (Exception e) {
            throw new BusinessException("Webhook validation failed");
        }

    }

    private String generateHmac(String payload, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec key =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        mac.init(key);

        byte[] rawHmac =
                mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(rawHmac);
    }

}
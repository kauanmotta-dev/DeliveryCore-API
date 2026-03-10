package com.douradelivery.after.service;

import com.douradelivery.after.model.order.enums.CancelReason;
import com.douradelivery.after.model.order.enums.CanceledBy;
import com.douradelivery.after.model.payment.entity.Payment;
import com.douradelivery.after.model.payment.enums.PaymentStatus;
import com.douradelivery.after.repository.OrderRepository;
import com.douradelivery.after.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentExpirationScheduler {

    private final PaymentRepository paymentRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expirePayments() {

        List<Payment> expiredPayments =
                paymentRepository.findByStatusAndExpiresAtBefore(
                        PaymentStatus.PENDING,
                        LocalDateTime.now()
                );

        if (expiredPayments.isEmpty()) {
            return;
        }

        log.info("Expiring {} payments", expiredPayments.size());

        for (Payment payment : expiredPayments) {

            payment.expire();

            payment.getOrder().cancel(
                    CanceledBy.SYSTEM,
                    CancelReason.SYSTEM_ERROR);

            log.info(
                    "Payment {} expired for order {}",
                    payment.getExternalPaymentId(),
                    payment.getOrder().getId()
            );
        }

        paymentRepository.saveAll(expiredPayments);
    }
}
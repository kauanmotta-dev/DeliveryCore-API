package com.douradelivery.after.service;

import com.douradelivery.after.config.webhook.WebhookValidator;
import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.order.enums.OrderEventType;
import com.douradelivery.after.model.order.enums.OrderStatus;
import com.douradelivery.after.model.payment.dto.PaymentCreateRequestDTO;
import com.douradelivery.after.model.payment.dto.PaymentResponseDTO;
import com.douradelivery.after.model.payment.dto.PaymentWebhookRequestDTO;
import com.douradelivery.after.model.payment.entity.Payment;
import com.douradelivery.after.model.payment.enums.PaymentStatus;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.repository.OrderRepository;
import com.douradelivery.after.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final WebhookValidator webhookValidator;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    private PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount()
        );
    }

    public PaymentResponseDTO createPayment(User client, Long orderId, PaymentCreateRequestDTO dto) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new BusinessException("Payment already exists for this order");
        }

        if (!order.getClient().getId().equals(client.getId())) {
            throw new BusinessException("You can only pay your own orders");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Cannot pay a canceled order");
        }

        if (!dto.paymentMethod().isEnabled()) {
            throw new BusinessException("Payment method not available");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(dto.paymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(dto.amount());
        payment.setCreatedAt(LocalDateTime.now());

        notificationService.notifyOrderEvent(
                payment.getOrder(),
                OrderEventType.PAYMENT_PENDING
        );
        return toResponse(paymentRepository.save(payment));
    }

    public void refundPayment(Order order, BigDecimal feePercentage) {

        Payment payment = paymentRepository.findByOrder(order)
                .orElse(null);

        if (payment == null) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return; // idempotente
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BusinessException("Payment is not refundable");
        }

        BigDecimal amount = payment.getAmount();

        BigDecimal feeAmount = amount.multiply(feePercentage);
        BigDecimal refundedAmount = amount.subtract(feeAmount);

        payment.setFeeAmount(feeAmount);
        payment.setRefundedAmount(refundedAmount);
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        notificationService.notifyOrderEvent(
                payment.getOrder(),
                OrderEventType.PAYMENT_REFUNDED
        );
    }

    public void handlePixWebhook(PaymentWebhookRequestDTO event) {

        webhookValidator.validate(event.secret());

        if (!"PAYMENT_CONFIRMED".equals(event.status())) {
            return; // ignorar eventos que não interessam
        }

        Payment payment = paymentRepository.findById(event.paymentId())
                .orElseThrow(() -> new BusinessException("Payment not found"));

        // idempotência
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        if (payment.getOrder().getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Order is canceled");
        }

        if (payment.getAmount().compareTo(event.amount()) != 0) {
            throw new BusinessException("Invalid payment amount");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        notificationService.notifyOrderEvent(
                payment.getOrder(),
                OrderEventType.PAYMENT_CONFIRMED
        );
    }
}


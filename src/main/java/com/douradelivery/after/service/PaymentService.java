package com.douradelivery.after.service;

import com.douradelivery.after.config.webhook.WebhookValidator;
import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.order.entity.Order;
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
import java.math.RoundingMode;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final WebhookValidator webhookValidator;

    @Transactional(readOnly = true)
    private PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount()
        );
    }

    public PaymentResponseDTO createPayment(User client, Long orderId, PaymentCreateRequestDTO dto, String idempotencyKey) {

        Optional<Payment> existingPayment =
                paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existingPayment.isPresent()) {
            return toResponse(existingPayment.get());
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getClient().getId().equals(client.getId())) {
            throw new BusinessException("You can only pay your own orders");
        }

        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new BusinessException("Order is not waiting for payment");
        }

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new BusinessException("Payment already exists for this order");
        }

        if (!dto.paymentMethod().isEnabled()) {
            throw new BusinessException("Payment method not available");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.initialize(dto.paymentMethod(), dto.amount(), idempotencyKey);

        paymentRepository.save(payment);

        return toResponse(payment);
    }

    public void processRefund(Order order, BigDecimal feePercentage) {

        Optional<Payment> optionalPayment = paymentRepository.findByOrder(order);

        if (optionalPayment.isEmpty()) {
            return;
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return; // idempotente
        }

        if (payment.getStatus() != PaymentStatus.CONFIRMED) {
            throw new BusinessException("Payment is not confirmed");
        }

        BigDecimal amount = payment.getAmount();

        BigDecimal feeAmount = amount.multiply(feePercentage)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal refundedAmount = amount.subtract(feeAmount)
                .setScale(2, RoundingMode.HALF_UP);

        payment.refund(refundedAmount, feeAmount);

        paymentRepository.save(payment);

        order.markAsRefunded();
    }

    public Long handlePixWebhook(PaymentWebhookRequestDTO event) {

        webhookValidator.validate(event.toString(), event.secret());

        if (!"PAYMENT_CONFIRMED".equals(event.status())) {
            return null;
        }

        Payment payment = paymentRepository.findById(event.paymentId())
                .orElseThrow(() -> new BusinessException("Payment not found"));

        Order order = payment.getOrder();

        // 🔒 1. Blindagem contra pedido cancelado/refundado
        if (order.getStatus() == OrderStatus.CANCELED ||
                order.getStatus() == OrderStatus.REFUNDED) {
            throw new BusinessException("Order already closed");
        }

        // 🔒 2. Blindagem contra pagamento duplicado
        if (payment.getStatus() == PaymentStatus.CONFIRMED) {
            return null; // idempotente
        }

        // 🔒 3. Só pode confirmar se estiver PENDING
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Invalid payment state");
        }

        // 🔒 4. Valor precisa bater exatamente
        if (payment.getAmount().compareTo(event.amount()) != 0) {
            throw new BusinessException("Invalid payment amount");
        }

        // 🔒 5. Pedido deve estar aguardando pagamento
        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new BusinessException("Order not waiting payment");
        }

        payment.confirm();
        paymentRepository.save(payment);

        return order.getId();
    }
}


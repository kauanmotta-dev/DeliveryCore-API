package com.deliverycore.after.model.payment.entity;

import com.deliverycore.after.exception.exceptions.BusinessException;
import com.deliverycore.after.model.payment.enums.PaymentMethod;
import com.deliverycore.after.model.payment.enums.PaymentStatus;
import com.deliverycore.after.model.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Setter
    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(unique = true)
    private String externalPaymentId;

    @Column(unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private BigDecimal amount;
    private BigDecimal refundedAmount;
    private BigDecimal feeAmount;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private LocalDateTime expiresAt;

    private String failureReason;

    public void initialize(PaymentMethod method, BigDecimal amount, String idempotencyKey) {

        if (this.status != null) {
            throw new BusinessException("Payment already initialized");
        }

        this.externalPaymentId = "pay_" + UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;

        this.method = method;
        this.amount = amount;

        this.status = PaymentStatus.PENDING;

        this.createdAt = LocalDateTime.now();

        this.expiresAt = LocalDateTime.now().plusMinutes(15);
    }

    public void confirm() {

        if (this.status != PaymentStatus.PENDING) {
            throw new BusinessException("Invalid payment transition");
        }

        if (LocalDateTime.now().isAfter(this.expiresAt)) {
            this.status = PaymentStatus.EXPIRED;
            throw new BusinessException("Payment expired");
        }

        this.status = PaymentStatus.CONFIRMED;
        this.paidAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {

        if (this.status != PaymentStatus.PENDING) {
            throw new BusinessException("Invalid payment state");
        }

        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public void expire() {

        if (this.status != PaymentStatus.PENDING) {
            return;
        }

        this.status = PaymentStatus.EXPIRED;
    }

    public void refund(BigDecimal refundedAmount, BigDecimal feeAmount) {

        if (this.status == PaymentStatus.REFUNDED) {
            return;
        }

        if (this.status != PaymentStatus.CONFIRMED) {
            throw new BusinessException("Only confirmed payments can be refunded");
        }

        this.status = PaymentStatus.REFUNDED;

        this.refundedAmount = refundedAmount;
        this.feeAmount = feeAmount;

        this.refundedAt = LocalDateTime.now();
    }
}
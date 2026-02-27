package com.douradelivery.after.model.payment.entity;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.payment.enums.PaymentMethod;
import com.douradelivery.after.model.payment.enums.PaymentStatus;
import com.douradelivery.after.model.order.entity.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public void initialize(PaymentMethod method, BigDecimal amount) {

        if (this.status != null) {
            throw new BusinessException("Payment already initialized");
        }

        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void confirm() {
        if (this.status != PaymentStatus.PENDING) {
            throw new BusinessException("Invalid payment transition");
        }
        this.status = PaymentStatus.CONFIRMED;
        this.paidAt = LocalDateTime.now();
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

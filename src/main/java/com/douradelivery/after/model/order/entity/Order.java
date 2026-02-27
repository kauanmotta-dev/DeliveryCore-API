package com.douradelivery.after.model.order.entity;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.order.enums.CancelReason;
import com.douradelivery.after.model.order.enums.CanceledBy;
import com.douradelivery.after.model.order.enums.OrderStatus;
import com.douradelivery.after.model.payment.entity.Payment;
import com.douradelivery.after.model.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Setter
    @ManyToOne(optional = false)
    private User client;

    @ManyToOne
    private User deliveryman;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Setter
    private String originAddress;
    @Setter
    private String destinationAddress;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    private CancelReason cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "canceled_by")
    private CanceledBy canceledBy;


    public void initialize() {
        this.status = OrderStatus.WAITING_PAYMENT;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsPaid() {
        if (this.status != OrderStatus.WAITING_PAYMENT) {
            throw new BusinessException("Order not waiting for payment");
        }
        this.status = OrderStatus.AVAILABLE;
    }

    public void accept(User deliveryman) {
        if (this.status != OrderStatus.AVAILABLE) {
            throw new BusinessException("Order not available");
        }
        this.deliveryman = deliveryman;
        this.status = OrderStatus.ACCEPTED;
    }

    public void startDelivery(User deliveryman) {
        if (this.status != OrderStatus.ACCEPTED) {
            throw new BusinessException("Order not accepted");
        }
        if (!this.deliveryman.getId().equals(deliveryman.getId())) {
            throw new BusinessException("Not your order");
        }
        this.status = OrderStatus.IN_DELIVERY;
    }

    public void deliver(User deliveryman) {
        if (this.status != OrderStatus.IN_DELIVERY) {
            throw new BusinessException("Order not in delivery");
        }
        if (!this.deliveryman.getId().equals(deliveryman.getId())) {
            throw new BusinessException("Not your order");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel(CanceledBy canceledBy, CancelReason reason) {

        if (this.status == OrderStatus.DELIVERED) {
            throw new BusinessException("Delivered orders cannot be canceled");
        }

        if (this.status == OrderStatus.REFUNDED) {
            throw new BusinessException("Order already refunded");
        }

        if (this.status == OrderStatus.CANCELED) {
            throw new BusinessException("Order already canceled");
        }

        this.canceledBy = canceledBy;
        this.cancelReason = reason;
        this.status = OrderStatus.CANCELED;
    }

    public void markAsRefunded() {
        if (this.status != OrderStatus.CANCELED) {
            throw new BusinessException("Order must be canceled before refund");
        }
        this.status = OrderStatus.REFUNDED;
    }

    public void withdrawDeliveryman(User deliveryman) {
        if (this.status != OrderStatus.ACCEPTED) {
            throw new BusinessException("Order not accepted");
        }
        if (!this.deliveryman.getId().equals(deliveryman.getId())) {
            throw new BusinessException("Not your order");
        }
        this.deliveryman = null;
        this.status = OrderStatus.AVAILABLE;
    }
}

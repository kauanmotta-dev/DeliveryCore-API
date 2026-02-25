package com.douradelivery.after.model.order.entity;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.order.enums.CancelReason;
import com.douradelivery.after.model.order.enums.CanceledBy;
import com.douradelivery.after.model.order.enums.OrderStatus;
import com.douradelivery.after.model.payment.entity.Payment;
import com.douradelivery.after.model.payment.enums.PaymentStatus;
import com.douradelivery.after.model.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static com.douradelivery.after.model.order.enums.OrderStatus.*;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "deliveryman_id")
    private User deliveryman;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private String originAddress;
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private OrderStatus status;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    private CancelReason cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "canceled_by")
    private CanceledBy canceledBy;

    public void initialize() {
        this.status = CREATED;
        this.createdAt = LocalDateTime.now();
    }

    private static final Map<OrderStatus, Set<OrderStatus>> allowedTransitions =
            Map.of(
                    CREATED, Set.of(ACCEPTED, CANCELED),
                    ACCEPTED, Set.of(IN_DELIVERY, CANCELED, CREATED),
                    IN_DELIVERY, Set.of(DELIVERED),
                    DELIVERED, Set.of(),
                    CANCELED, Set.of()
            );

    public void changeStatus(OrderStatus newStatus) {

        if (this.status == null) {
            throw new BusinessException("Order not initialized");
        }

        if (!allowedTransitions.get(this.status).contains(newStatus)) {
            throw new BusinessException("Invalid order status transition");
        }

        if (newStatus == ACCEPTED) {
            if (this.payment == null ||
                    this.payment.getStatus() != PaymentStatus.PAID) {
                throw new BusinessException("Order must be paid before acceptance");
            }
        }

        if (newStatus == DELIVERED) {
            if (this.payment == null ||
                    this.payment.getStatus() != PaymentStatus.PAID) {
                throw new BusinessException("Cannot deliver unpaid order");
            }
        }

        this.status = newStatus;
    }
}

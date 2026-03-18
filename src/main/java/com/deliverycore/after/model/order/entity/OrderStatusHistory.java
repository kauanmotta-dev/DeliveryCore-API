package com.deliverycore.after.model.order.entity;

import com.deliverycore.after.model.order.enums.CancelReason;
import com.deliverycore.after.model.order.enums.CanceledBy;
import com.deliverycore.after.model.order.enums.OrderStatus;
import com.deliverycore.after.model.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "changed_by_user_id")
    private User changedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    private CancelReason cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "canceled_by")
    private CanceledBy canceledBy;
}

package com.deliverycore.after.service;

import com.deliverycore.after.model.deliveryLocation.dto.DeliveryLocationEventDTO;
import com.deliverycore.after.model.deliverymanVerification.dto.DeliverymanVerificationEventDTO;
import com.deliverycore.after.model.deliverymanVerification.enums.VerificationStatus;
import com.deliverycore.after.model.order.dto.OrderStatusEventDTO;
import com.deliverycore.after.model.order.entity.Order;
import com.deliverycore.after.model.order.enums.OrderEventType;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.util.TransactionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyOrderEventAfterCommit(Order order, OrderEventType type) {

        TransactionUtils.runAfterCommit(() -> {

            OrderStatusEventDTO event = new OrderStatusEventDTO(
                    type,
                    order.getId(),
                    order.getStatus().name()
            );

            messagingTemplate.convertAndSendToUser(
                    order.getClient().getEmail(),
                    "/queue/orders",
                    event
            );

            if (order.getDeliveryman() != null) {
                messagingTemplate.convertAndSendToUser(
                        order.getDeliveryman().getEmail(),
                        "/queue/orders",
                        event
                );
            }

            messagingTemplate.convertAndSend(
                    "/topic/admin/orders",
                    event
            );
        });
    }

    public void notifyDeliverymanVerificationAfterCommit(User user, VerificationStatus status) {

        TransactionUtils.runAfterCommit(() -> {

            DeliverymanVerificationEventDTO event =
                    new DeliverymanVerificationEventDTO(status);

            messagingTemplate.convertAndSendToUser(
                    user.getEmail(),
                    "/queue/verification",
                    event
            );

            messagingTemplate.convertAndSend(
                    "/topic/admin/verification",
                    event
            );
        });
    }

    public void notifyLocationUpdateAfterCommit(
            Order order,
            Double latitude,
            Double longitude
    ) {

        TransactionUtils.runAfterCommit(() -> {

            DeliveryLocationEventDTO event = new DeliveryLocationEventDTO(
                    order.getId(),
                    latitude,
                    longitude
            );

            messagingTemplate.convertAndSendToUser(
                    order.getClient().getEmail(),
                    "/queue/location",
                    event
            );

            messagingTemplate.convertAndSend(
                    "/topic/admin/location",
                    event
            );

        });
    }

    public void notifyNewAvailableOrder(User deliveryman, Order order) {

        TransactionUtils.runAfterCommit(() -> {

            messagingTemplate.convertAndSendToUser(
                    deliveryman.getEmail(),
                    "/queue/new-orders",
                    order.getId()
            );

        });
    }
}
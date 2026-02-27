package com.douradelivery.after.service;

import com.douradelivery.after.model.order.dto.OrderStatusEventDTO;
import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.order.enums.OrderEventType;
import com.douradelivery.after.util.TransactionUtils;
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
}
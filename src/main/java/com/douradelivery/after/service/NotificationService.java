package com.douradelivery.after.service;

import com.douradelivery.after.model.order.dto.OrderStatusEventDTO;
import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.order.enums.OrderEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyOrderEvent(Order order, OrderEventType type) {

        OrderStatusEventDTO event = new OrderStatusEventDTO(
                type,
                order.getId(),
                order.getStatus().name()
        );

        // CLIENTE
        messagingTemplate.convertAndSendToUser(
                order.getClient().getEmail(),
                "/queue/orders",
                event
        );

        // ENTREGADOR (se existir)
        if (order.getDeliveryman() != null) {
            messagingTemplate.convertAndSendToUser(
                    order.getDeliveryman().getEmail(),
                    "/queue/orders",
                    event
            );
        }

        // ADMIN (opcional)
        messagingTemplate.convertAndSend(
                "/topic/admin/orders",
                event
        );
    }
}
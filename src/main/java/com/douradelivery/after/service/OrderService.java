package com.douradelivery.after.service;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.order.dto.OrderCreateWithPaymentRequestDTO;
import com.douradelivery.after.model.order.dto.OrderResponseDTO;
import com.douradelivery.after.model.order.dto.OrderStatusEventDTO;
import com.douradelivery.after.model.order.dto.OrderStatusHistoryResponseDTO;
import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.order.enums.CancelReason;
import com.douradelivery.after.model.order.enums.CanceledBy;
import com.douradelivery.after.model.order.enums.OrderEventType;
import com.douradelivery.after.model.order.enums.OrderStatus;
import com.douradelivery.after.model.order.entity.OrderStatusHistory;
import com.douradelivery.after.model.payment.dto.PaymentCreateRequestDTO;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.repository.OrderRepository;
import com.douradelivery.after.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final SlaService slaService;

    @Transactional(readOnly = true)
    private OrderResponseDTO toResponse(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOriginAddress(),
                order.getDestinationAddress(),
                order.getStatus()
        );
    }


    public OrderResponseDTO createOrder(User client, OrderCreateWithPaymentRequestDTO dto) {

        Order order = new Order();
        order.setClient(client);
        order.setOriginAddress(dto.originAddress());
        order.setDestinationAddress(dto.destinationAddress());
        order.initialize();

        orderRepository.save(order);

        paymentService.createPayment(
                client,
                order.getId(),
                new PaymentCreateRequestDTO(
                        dto.amount(),
                        dto.paymentMethod()
                )
        );

        registerHistory(order, OrderStatus.CREATED, client);
        notificationService.notifyOrderEvent(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
        return toResponse(order);
    }

    public void cancelOrderByClient(User client, Long orderId) {

        BigDecimal feePercentage;

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getClient().getId().equals(client.getId())) {
            throw new BusinessException("You can only cancel your own orders");
        }

        OrderStatus previousStatus = order.getStatus();

        if (previousStatus == OrderStatus.CREATED) {
            feePercentage = BigDecimal.ZERO;
        }
        else if (previousStatus == OrderStatus.ACCEPTED) {
            feePercentage = new  BigDecimal("0.10");
        }
        else if (previousStatus == OrderStatus.IN_DELIVERY) {
            slaService.registerClientCancellation(client);
            feePercentage = new  BigDecimal("0.30");
        }
        else  {throw new BusinessException("Order cannot be canceled");}

        paymentService.refundPayment(order, feePercentage);

        order.setCanceledBy(CanceledBy.CLIENT);
        order.setCancelReason(
                previousStatus == OrderStatus.IN_DELIVERY
                ? CancelReason.CLIENT_AFTER_ROUTE
                : CancelReason.CLIENT_REQUEST
        );
        order.changeStatus(OrderStatus.CANCELED);

        orderRepository.save(order);
        registerHistory(order, OrderStatus.CANCELED, client);
        notificationService.notifyOrderEvent(
                order,
                OrderEventType.ORDER_CANCELED
        );
    }

    public void acceptOrder(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Order is not available");
        }

        order.changeStatus(OrderStatus.ACCEPTED);
        order.setDeliveryman(deliveryman);

        orderRepository.save(order);
        registerHistory(order, OrderStatus.ACCEPTED, deliveryman);

        notificationService.notifyOrderEvent(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }

    public void withdrawalByDeliveryman(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getDeliveryman().getId().equals(deliveryman.getId())) {
            throw new BusinessException("You can only cancel your own orders");
        }

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new BusinessException("Order cannot be canceled at this stage");
        }

        slaService.registerDeliverymanWithdrawal(deliveryman);

        order.setDeliveryman(null);
        order.changeStatus(OrderStatus.CREATED);

        orderRepository.save(order);
        registerHistory(order, OrderStatus.CREATED, deliveryman);
        notificationService.notifyOrderEvent(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }

    public void startDelivery(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getDeliveryman().getId().equals(deliveryman.getId())) {
            throw new BusinessException("You can only start delivery for your own orders");
        }

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new BusinessException("Order is not ready to start delivery");
        }

        order.changeStatus(OrderStatus.IN_DELIVERY);

        orderRepository.save(order);
        registerHistory(order, OrderStatus.IN_DELIVERY, deliveryman);
        notificationService.notifyOrderEvent(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }

    public void deliverOrder(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getDeliveryman().getId().equals(deliveryman.getId())) {
            throw new BusinessException("You can only deliver your own orders");
        }

        if (order.getStatus() != OrderStatus.IN_DELIVERY) {
            throw new BusinessException("Order is not in route");
        }

        order.changeStatus(OrderStatus.DELIVERED);

        orderRepository.save(order);
        registerHistory(order, OrderStatus.DELIVERED, deliveryman);
        notificationService.notifyOrderEvent(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }


    private void registerHistory(Order order, OrderStatus status, User user) {

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedBy(user);
        history.setChangedAt(LocalDateTime.now());

        history.setCanceledBy(order.getCanceledBy());
        history.setCancelReason(order.getCancelReason());

        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponseDTO> getOrderHistory(User user, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (user.getRole().name().equals("CLIENT")
                && !order.getClient().getId().equals(user.getId())) {
            throw new BusinessException("Access denied");
        }

        return historyRepository.findByOrderOrderByChangedAtAsc(order)
                .stream()
                .map(h -> new OrderStatusHistoryResponseDTO(
                        h.getStatus(),
                        h.getChangedBy() != null ? h.getChangedBy().getName() : null,
                        h.getChangedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> listAvailableOrders() {
        return orderRepository.findByStatus(OrderStatus.CREATED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderStatusEventDTO> getActiveOrdersForClient(User client) {

        List<Order> orders = orderRepository
                .findByClientAndStatusIn(
                        client,
                        List.of(
                                OrderStatus.CREATED,
                                OrderStatus.ACCEPTED,
                                OrderStatus.IN_DELIVERY
                        )
                );

        return orders.stream()
                .map(order -> new OrderStatusEventDTO(
                        OrderEventType.ORDER_STATUS_CHANGED,
                        order.getId(),
                        order.getStatus().name()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> listMyOrders(User client) {
        return orderRepository.findByClient(client)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> listMyDeliveries(User deliveryman) {
        return orderRepository.findByDeliveryman(deliveryman)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> listAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}

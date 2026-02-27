package com.douradelivery.after.service;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.order.dto.OrderCreateRequestDTO;
import com.douradelivery.after.model.order.dto.OrderResponseDTO;
import com.douradelivery.after.model.order.dto.OrderStatusEventDTO;
import com.douradelivery.after.model.order.dto.OrderStatusHistoryResponseDTO;
import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.order.entity.OrderStatusHistory;
import com.douradelivery.after.model.order.enums.CancelReason;
import com.douradelivery.after.model.order.enums.CanceledBy;
import com.douradelivery.after.model.order.enums.OrderEventType;
import com.douradelivery.after.model.order.enums.OrderStatus;
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

    public OrderResponseDTO createOrder(User client, OrderCreateRequestDTO dto) {

        Order order = new Order();
        order.setClient(client);
        order.setOriginAddress(dto.originAddress());
        order.setDestinationAddress(dto.destinationAddress());
        order.initialize();

        orderRepository.save(order);

        registerHistory(order, OrderStatus.WAITING_PAYMENT, client);

        return toResponse(order);
    }

    public void cancelOrderByClient(User client, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (!order.getClient().getId().equals(client.getId())) {
            throw new BusinessException("You can only cancel your own orders");
        }

        OrderStatus previousStatus = order.getStatus();

        BigDecimal feePercentage = calculateFee(previousStatus);

        if (previousStatus == OrderStatus.IN_DELIVERY) {
            slaService.registerClientCancellation(client);
        }

        order.cancel(
                CanceledBy.CLIENT,
                previousStatus == OrderStatus.IN_DELIVERY
                        ? CancelReason.CLIENT_AFTER_ROUTE
                        : CancelReason.CLIENT_REQUEST
        );

        paymentService.processRefund(order, feePercentage);

        orderRepository.save(order);

        registerHistory(order, OrderStatus.CANCELED, client);

        notificationService.notifyOrderEventAfterCommit(
                order,
                OrderEventType.ORDER_CANCELED
        );
    }

    public void acceptOrder(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        order.accept(deliveryman);

        orderRepository.save(order);

        registerHistory(order, OrderStatus.ACCEPTED, deliveryman);
        notificationService.notifyOrderEventAfterCommit(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }

    public void withdrawalByDeliveryman(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        order.withdrawDeliveryman(deliveryman);

        orderRepository.save(order);

        slaService.registerDeliverymanWithdrawal(deliveryman);

        registerHistory(order, OrderStatus.AVAILABLE, deliveryman);
        notificationService.notifyOrderEventAfterCommit(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }

    public void startDelivery(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        order.startDelivery(deliveryman);

        orderRepository.save(order);

        registerHistory(order, OrderStatus.IN_DELIVERY, deliveryman);
        notificationService.notifyOrderEventAfterCommit(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }

    public void deliverOrder(User deliveryman, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        order.deliver(deliveryman);

        orderRepository.save(order);

        registerHistory(order, OrderStatus.DELIVERED, deliveryman);
        notificationService.notifyOrderEventAfterCommit(
                order,
                OrderEventType.ORDER_STATUS_CHANGED
        );
    }

    public void markAsPaid(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        order.markAsPaid();
        orderRepository.save(order);

        registerHistory(order, OrderStatus.AVAILABLE, null);
        notificationService.notifyOrderEventAfterCommit(
                order,
                OrderEventType.PAYMENT_CONFIRMED
        );
    }

    private BigDecimal calculateFee(OrderStatus status) {

        return switch (status) {

            case WAITING_PAYMENT -> BigDecimal.ZERO;

            case AVAILABLE -> BigDecimal.ZERO;

            case ACCEPTED -> new BigDecimal("0.10");

            case IN_DELIVERY -> new BigDecimal("0.30");

            default -> throw new BusinessException("Order cannot be canceled");
        };
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
        return orderRepository.findByStatus(OrderStatus.AVAILABLE)
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
                                OrderStatus.AVAILABLE,
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

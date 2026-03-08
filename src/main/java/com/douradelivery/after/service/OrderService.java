package com.douradelivery.after.service;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.audit.enums.AuditLogAction;
import com.douradelivery.after.model.deliverymanVerification.entity.DeliverymanVerification;
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
import com.douradelivery.after.model.payment.enums.PaymentStatus;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.user.enums.UserRole;
import com.douradelivery.after.repository.DeliverymanVerificationRepository;
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
    private final DeliverymanVerificationRepository verificationRepository;
    private final AuditLogService auditLogService;

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
        auditLogService.log(
                AuditLogAction.ORDER_CREATED,
                "Order",
                order.getId(),
                client.getId(),
                "Order created"
        );

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

        if(previousStatus == OrderStatus.WAITING_PAYMENT) {
            registerHistory(order, OrderStatus.CANCELED, client);
            notificationService.notifyOrderEventAfterCommit(
                    order,
                    OrderEventType.ORDER_CANCELED
            );
        } else{
            order.markAsRefunded();
            registerHistory(order, OrderStatus.REFUNDED, client);
            auditLogService.log(
                    AuditLogAction.ORDER_CANCELED,
                    "Order",
                    order.getId(),
                    client.getId(),
                    "Order canceled by client"
            );

            notificationService.notifyOrderEventAfterCommit(
                    order,
                    OrderEventType.PAYMENT_REFUNDED
            );
        }
        orderRepository.save(order);
    }

    public void acceptOrder(User deliveryman, Long orderId) {

        Order order = orderRepository
                .findWithLockById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (deliveryman.getRole() != UserRole.DELIVERYMAN) {
            throw new BusinessException("User is not a deliveryman");
        }

        DeliverymanVerification verification =
                verificationRepository
                        .findTopByUserOrderByCreatedAtDesc(deliveryman)
                        .orElseThrow(() ->
                                new BusinessException("Deliveryman verification not found"));

        verification.ensureApproved();

        order.accept(deliveryman);

        orderRepository.save(order);

        registerHistory(order, OrderStatus.ACCEPTED, deliveryman);
        auditLogService.log(
                AuditLogAction.ORDER_ACCEPTED,
                "Order",
                order.getId(),
                deliveryman.getId(),
                "Order accepted by deliveryman"
        );

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
        auditLogService.log(
                AuditLogAction.ORDER_CANCELED,
                "Order",
                order.getId(),
                deliveryman.getId(),
                "Deliveryman withdrew from order"
        );

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
        auditLogService.log(
                AuditLogAction.ORDER_STARTED,
                "Order",
                order.getId(),
                deliveryman.getId(),
                "Delivery started"
        );

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
        auditLogService.log(
                AuditLogAction.ORDER_DELIVERED,
                "Order",
                order.getId(),
                deliveryman.getId(),
                "Order delivered"
        );

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

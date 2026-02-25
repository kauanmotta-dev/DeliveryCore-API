package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.order.dto.*;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Cliente cria o pedido pré-pago")
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/create/pré-pago")
    public ApiResponse<OrderResponseDTO> createPrePaid(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid OrderCreateWithPaymentRequestDTO dto
    ) {
        return ApiResponse.success(orderService.createOrder(user, dto));
    }

    @Operation(summary = "Cliente cancela o pedido antes de ser aceito pelo Deliverman")
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelByClient(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        orderService.cancelOrderByClient(user, id);
        return ApiResponse.success("Order canceled");
    }

    @Operation(summary = "Deliveryman aceita o pedido")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    @PostMapping("/{id}/accept")
    public ApiResponse<Void> accept(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        orderService.acceptOrder(user, id);
        return ApiResponse.success("Order accepted");
    }

    @Operation(summary = "Deliveryman cancela ser o entregador de pedido antes de sair para entrega")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    @PostMapping("/{id}/cancel-by-deliveryman")
    public ApiResponse<Void> withdrawalByDeliveryman(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        orderService.withdrawalByDeliveryman(user, id);
        return ApiResponse.success("Order returned to available list");
    }


    @Operation(summary = "Deliveryman sai para entregar o pedido")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    @PostMapping("/{id}/start")
    public ApiResponse<Void> start(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        orderService.startDelivery(user, id);
        return ApiResponse.success("Order is now in route");
    }


    @Operation(summary = "Deliveryman entrega o pedido")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    @PostMapping("/{id}/deliver")
    public ApiResponse<Void> deliver(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        orderService.deliverOrder(user, id);
        return ApiResponse.success("Order delivered");
    }

    @Operation(summary = "Lista o histórico do pedido")
    @PreAuthorize("hasAnyRole('CLIENT','DELIVERYMAN','ADMIN')")
    @GetMapping("/{id}/history")
    public ApiResponse<List<OrderStatusHistoryResponseDTO>> history(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        return ApiResponse.success(orderService.getOrderHistory(user, id));
    }

    @Operation(summary = "Verifica todos os pedidos disponiveis")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    @GetMapping("/available")
    public ApiResponse<List<OrderResponseDTO>> available() {
        return ApiResponse.success(orderService.listAvailableOrders());
    }

    @Operation(summary = "Retorna todos os pedidos ativos do cliente")
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me/active")
    public ApiResponse<List<OrderStatusEventDTO>> getMyActiveOrders(
            @AuthenticationPrincipal User client
    ) {
        return ApiResponse.success(
                orderService.getActiveOrdersForClient(client)
        );
    }

    @Operation(summary = "Cliente verifica todos os seus pedidos")
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me")
    public ApiResponse<List<OrderResponseDTO>> myOrders(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.success(orderService.listMyOrders(user));
    }

    @Operation(summary = "Deliveryman verifica todas as suas entregas")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    @GetMapping("/my-deliveries")
    public ApiResponse<List<OrderResponseDTO>> myDeliveries(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.success(orderService.listMyDeliveries(user));
    }

    @Operation(summary = "Admin verifica todos os pedidos")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<OrderResponseDTO>> listAll() {
        return ApiResponse.success(orderService.listAllOrders());
    }

}

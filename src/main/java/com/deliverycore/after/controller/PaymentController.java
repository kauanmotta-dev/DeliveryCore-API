package com.deliverycore.after.controller;

import com.deliverycore.after.exception.response.ApiResponse;
import com.deliverycore.after.model.payment.dto.PaymentCreateRequestDTO;
import com.deliverycore.after.model.payment.dto.PaymentResponseDTO;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pagamentos")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Cria o pedido de pagamento")
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/order/{orderId}")
    public ApiResponse<PaymentResponseDTO> create(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId,
            @RequestBody PaymentCreateRequestDTO dto,
            @RequestHeader("Idempotency-Key") String idempotencyKey

    ) {
        return ApiResponse.success(paymentService.createPayment(user, orderId, dto, idempotencyKey));
    }
}

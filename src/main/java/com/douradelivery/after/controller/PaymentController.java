package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.payment.dto.PaymentCreateRequestDTO;
import com.douradelivery.after.model.payment.dto.PaymentResponseDTO;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.service.PaymentService;
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
            @RequestBody PaymentCreateRequestDTO dto
    ) {
        return ApiResponse.success(paymentService.createPayment(user, orderId, dto));
    }
}

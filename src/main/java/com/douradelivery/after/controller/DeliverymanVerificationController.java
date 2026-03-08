package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.deliverymanVerification.dto.DeliverymanVerificationRequestDTO;
import com.douradelivery.after.service.DeliverymanVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Verificação de Entregadores")
@RestController
@RequestMapping("/deliveryman-verification")
@RequiredArgsConstructor
public class DeliverymanVerificationController {

    private final DeliverymanVerificationService service;

    @Operation(summary = "Solicitar verificação como entregador")
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ApiResponse<Void> requestVerification(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid DeliverymanVerificationRequestDTO dto
    ) {

        service.requestVerification(user, dto);

        return ApiResponse.success("Verification request submitted");
    }
}
package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.deliverymanVerification.dto.DeliverymanVerificationResponseDTO;
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

import java.util.List;

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


    @Operation(summary = "Lista todas as verificações")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DeliverymanVerificationResponseDTO>> listAll() {

        return ApiResponse.success(
                service.listAllVerifications()
        );
    }

    @Operation(summary = "Ver detalhes da verificação")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DeliverymanVerificationResponseDTO> getById(
            @PathVariable Long id
    ) {

        return ApiResponse.success(
                service.getVerification(id)
        );
    }

    @Operation(summary = "Aprovar verificação")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin
    ) {

        service.approveVerification(id, admin);

        return ApiResponse.success("Verification approved");
    }

    @Operation(summary = "Rejeitar verificação")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin
    ) {

        service.rejectVerification(id, admin);

        return ApiResponse.success("Verification rejected");
    }

    @Operation(summary = "Suspender deliveryman")
    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> suspend(
            @PathVariable Long id,
            @RequestParam String reason,
            @AuthenticationPrincipal User admin
    ) {

        service.suspendDeliveryman(id, admin, reason);

        return ApiResponse.success("Deliveryman suspended");
    }
}
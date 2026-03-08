package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.deliverymanVerification.dto.DeliverymanVerificationResponseDTO;
import com.douradelivery.after.service.DeliverymanVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Deliveryman Verification - Admin")
@RestController
@RequestMapping("/admin/deliveryman-verifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DeliverymanVerificationAdminController {

    private final DeliverymanVerificationService service;

    @Operation(summary = "Lista todas as verificações")
    @GetMapping
    public ApiResponse<List<DeliverymanVerificationResponseDTO>> listAll() {

        return ApiResponse.success(
                service.listAllVerifications()
        );
    }

    @Operation(summary = "Ver detalhes da verificação")
    @GetMapping("/{id}")
    public ApiResponse<DeliverymanVerificationResponseDTO> getById(
            @PathVariable Long id
    ) {

        return ApiResponse.success(
                service.getVerification(id)
        );
    }

    @Operation(summary = "Aprovar verificação")
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin
    ) {

        service.approveVerification(id, admin);

        return ApiResponse.success("Verification approved");
    }

    @Operation(summary = "Rejeitar verificação")
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin
    ) {

        service.rejectVerification(id, admin);

        return ApiResponse.success("Verification rejected");
    }

    @Operation(summary = "Suspender deliveryman")
    @PostMapping("/{id}/suspend")
    public ApiResponse<Void> suspend(
            @PathVariable Long id,
            @RequestParam String reason,
            @AuthenticationPrincipal User admin
    ) {

        service.suspendDeliveryman(id, admin, reason);

        return ApiResponse.success("Deliveryman suspended");
    }
}
package com.deliverycore.after.controller;

import com.deliverycore.after.exception.response.ApiResponse;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.service.DeliverymanStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Deliveryman Status")
@RestController
@RequestMapping("/deliveryman/status")
@RequiredArgsConstructor
public class DeliverymanStatusController {

    private final DeliverymanStatusService service;

    @PostMapping("/online")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    public ApiResponse<Void> goOnline(@AuthenticationPrincipal User user) {

        service.goOnline(user);

        return ApiResponse.success("Deliveryman is now ONLINE");
    }

    @PostMapping("/offline")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    public ApiResponse<Void> goOffline(@AuthenticationPrincipal User user) {

        service.goOffline(user);

        return ApiResponse.success("Deliveryman is now OFFLINE");
    }
}
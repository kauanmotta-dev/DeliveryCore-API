package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.deliveryLocation.dto.DeliveryLocationUpdateDTO;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.service.DeliveryLocationService;
import com.douradelivery.after.service.DeliveryLocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Location")
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class DeliveryCurrentLocationController {

    private final DeliveryLocationService locationService;

    @PostMapping("/order/{orderId}")
    @PreAuthorize("hasRole('DELIVERYMAN')")
    public ApiResponse<Void> updateLocation(

            @AuthenticationPrincipal User deliveryman,
            @PathVariable Long orderId,
            @RequestBody @Valid DeliveryLocationUpdateDTO dto
    ) {

        locationService.updateLocation(deliveryman, orderId, dto);

        return ApiResponse.success("Location updated");
    }
}
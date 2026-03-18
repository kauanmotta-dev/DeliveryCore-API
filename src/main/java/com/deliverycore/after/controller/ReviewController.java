package com.deliverycore.after.controller;

import com.deliverycore.after.model.review.dto.ReviewRequestDTO;
import com.deliverycore.after.model.review.dto.ReviewResponseDTO;
import com.deliverycore.after.model.review.dto.UserRatingDTO;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.exception.response.ApiResponse;
import com.deliverycore.after.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Usuário avalia outro Usuario relacionado a entrega")
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT','DELIVERYMAN')")
    public ApiResponse<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewRequestDTO dto,
            @AuthenticationPrincipal User user) {

        return ApiResponse.success(reviewService.createReview(dto, user));
    }

    @Operation(summary = "Retorna as avalições do Usuário")
    @GetMapping("/users/{userId}/rating")
    public ApiResponse<UserRatingDTO> getUserRating(@PathVariable Long userId) {
        return ApiResponse.success(reviewService.getUserRating(userId));
    }
}

package com.deliverycore.after.model.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long id;
    private Long orderId;
    private Long reviewerId;
    private Long reviewedId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

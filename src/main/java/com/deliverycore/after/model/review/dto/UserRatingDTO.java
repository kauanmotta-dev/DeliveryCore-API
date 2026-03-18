package com.deliverycore.after.model.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.internal.build.AllowPrintStacktrace;

@Getter
@AllArgsConstructor
public class UserRatingDTO {

    private Long userId;
    private Double averageRating;
    private Long totalReviews;
}

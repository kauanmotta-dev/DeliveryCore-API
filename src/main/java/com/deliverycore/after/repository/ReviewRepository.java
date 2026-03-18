package com.deliverycore.after.repository;

import com.deliverycore.after.model.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrderIdAndReviewerId(Long orderId, Long reviewerId);

    @Query("""
        SELECT AVG(r.rating), COUNT(r)
        FROM Review r
        WHERE r.reviewed.id = :userId
    """)
    Object[] calculateUserRating(Long userId);
}

package com.deliverycore.after.service;

import com.deliverycore.after.exception.exceptions.BusinessException;
import com.deliverycore.after.model.order.entity.Order;
import com.deliverycore.after.model.order.enums.OrderStatus;
import com.deliverycore.after.model.review.dto.ReviewRequestDTO;
import com.deliverycore.after.model.review.dto.ReviewResponseDTO;
import com.deliverycore.after.model.review.dto.UserRatingDTO;
import com.deliverycore.after.model.review.entity.Review;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.repository.OrderRepository;
import com.deliverycore.after.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewResponseDTO createReview(ReviewRequestDTO dto, User authenticatedUser) {

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("Order not delivered yet");
        }

        boolean isClient = order.getClient().getId().equals(authenticatedUser.getId());
        boolean isDeliveryman = order.getDeliveryman() != null &&
                order.getDeliveryman().getId().equals(authenticatedUser.getId());

        if (!isClient && !isDeliveryman) {
            throw new BusinessException("User not part of this order");
        }

        if (reviewRepository.existsByOrderIdAndReviewerId(order.getId(), authenticatedUser.getId())) {
            throw new BusinessException("Review already created for this order");
        }

        User reviewed = isClient ? order.getDeliveryman() : order.getClient();

        if (reviewed == null) {
            throw new BusinessException("Reviewed user not available");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setReviewer(authenticatedUser);
        review.setReviewed(reviewed);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        return new ReviewResponseDTO(
                review.getId(),
                order.getId(),
                authenticatedUser.getId(),
                reviewed.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public UserRatingDTO getUserRating(Long userId) {

        Object[] result = reviewRepository.calculateUserRating(userId);

        Double average = result[0] != null ? (Double) result[0] : 0.0;
        Long total = result[1] != null ? (Long) result[1] : 0L;

        return new UserRatingDTO(userId, average, total);
    }
}

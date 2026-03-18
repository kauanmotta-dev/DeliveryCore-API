package com.deliverycore.after.repository;

import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.model.deliverymanVerification.entity.DeliverymanVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliverymanVerificationRepository
        extends JpaRepository<DeliverymanVerification, Long> {

    Optional<DeliverymanVerification> findTopByUserOrderByCreatedAtDesc(User user);

}
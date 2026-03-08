package com.douradelivery.after.repository;

import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.deliverymanVerification.entity.DeliverymanVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliverymanVerificationRepository
        extends JpaRepository<DeliverymanVerification, Long> {

    Optional<DeliverymanVerification> findTopByUserOrderByCreatedAtDesc(User user);

}
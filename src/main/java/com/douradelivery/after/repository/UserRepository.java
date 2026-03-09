package com.douradelivery.after.repository;


import com.douradelivery.after.model.payment.entity.Payment;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.user.enums.DeliverymanStatus;
import com.douradelivery.after.model.user.enums.UserRole;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<User> findByRoleAndDeliverymanStatus(UserRole userRole, DeliverymanStatus deliverymanStatus);
}

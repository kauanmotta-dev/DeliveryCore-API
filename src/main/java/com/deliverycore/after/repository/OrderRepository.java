package com.deliverycore.after.repository;

import com.deliverycore.after.model.order.entity.Order;
import com.deliverycore.after.model.order.enums.OrderStatus;
import com.deliverycore.after.model.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Order> findWithLockById(Long id);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByClient(User client);

    List<Order> findByDeliveryman(User deliveryman);

    List<Order> findByClientAndStatusIn(
            User client,
            List<OrderStatus> statuses
    );
}

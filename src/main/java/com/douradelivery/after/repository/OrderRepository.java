package com.douradelivery.after.repository;

import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.order.enums.OrderStatus;
import com.douradelivery.after.model.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByClient(User client);

    List<Order> findByDeliveryman(User deliveryman);

    List<Order> findByClientAndStatusIn(
            User client,
            List<OrderStatus> statuses
    );
}

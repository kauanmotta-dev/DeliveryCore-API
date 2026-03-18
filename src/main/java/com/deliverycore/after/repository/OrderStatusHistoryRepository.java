package com.deliverycore.after.repository;

import com.deliverycore.after.model.order.entity.Order;
import com.deliverycore.after.model.order.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository
        extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrderOrderByChangedAtAsc(Order order);
}

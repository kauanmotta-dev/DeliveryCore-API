package com.douradelivery.after.service;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.deliveryLocation.dto.DeliveryLocationUpdateDTO;
import com.douradelivery.after.model.deliveryLocation.entity.DeliverymanCurrentLocation;
import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.order.enums.OrderStatus;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.repository.DeliverymanCurrentLocationRepository;
import com.douradelivery.after.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryLocationService {

    private static final int MIN_UPDATE_SECONDS = 5;

    private final DeliverymanCurrentLocationRepository locationRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public void updateLocation(
            User deliveryman,
            Long orderId,
            DeliveryLocationUpdateDTO dto
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (order.getDeliveryman() == null ||
                !order.getDeliveryman().getId().equals(deliveryman.getId())) {
            throw new BusinessException("Not your order");
        }

        if (order.getStatus() != OrderStatus.IN_DELIVERY) {
            throw new BusinessException("Order is not in delivery");
        }

        DeliverymanCurrentLocation location =
                locationRepository.findById(deliveryman.getId())
                        .orElseGet(() -> {

                            DeliverymanCurrentLocation newLocation =
                                    new DeliverymanCurrentLocation();

                            newLocation.setDeliveryman(deliveryman);

                            return newLocation;
                        });

        LocalDateTime now = LocalDateTime.now();

        if (location.getUpdatedAt() != null) {

            long seconds =
                    Duration.between(
                            location.getUpdatedAt(),
                            now
                    ).getSeconds();

            if (seconds < MIN_UPDATE_SECONDS) {
                return;
            }
        }

        location.setLatitude(dto.latitude());
        location.setLongitude(dto.longitude());
        location.setUpdatedAt(now);

        locationRepository.save(location);

        notificationService.notifyLocationUpdateAfterCommit(
                order,
                dto.latitude(),
                dto.longitude()
        );
    }
}
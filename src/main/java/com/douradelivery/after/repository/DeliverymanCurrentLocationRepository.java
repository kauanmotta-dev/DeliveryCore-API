package com.douradelivery.after.repository;

import com.douradelivery.after.model.deliveryLocation.entity.DeliverymanCurrentLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverymanCurrentLocationRepository
        extends JpaRepository<DeliverymanCurrentLocation, Long> {
}
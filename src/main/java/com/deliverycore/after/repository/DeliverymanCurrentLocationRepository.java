package com.deliverycore.after.repository;

import com.deliverycore.after.model.deliveryLocation.entity.DeliverymanCurrentLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverymanCurrentLocationRepository
        extends JpaRepository<DeliverymanCurrentLocation, Long> {
}
package com.douradelivery.after.service;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.deliveryLocation.entity.DeliverymanCurrentLocation;
import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.repository.DeliverymanCurrentLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryGeofenceService {

    private static final double MAX_DISTANCE_METERS = 100;

    private final DeliverymanCurrentLocationRepository locationRepository;
    private final DistanceService distanceService;

    public void validateDeliveryLocation(Order order) {

        Long deliverymanId = order.getDeliveryman().getId();

        DeliverymanCurrentLocation location =
                locationRepository.findById(deliverymanId)
                        .orElseThrow(() ->
                                new BusinessException("Deliveryman location not found"));

        double destinationLat = order.getDestinationAddress().getLatitude();
        double destinationLon = order.getDestinationAddress().getLongitude();

        double distanceKm =
                distanceService.calculate(
                        destinationLat,
                        destinationLon,
                        location.getLatitude(),
                        location.getLongitude()
                );

        double distanceMeters = distanceKm * 1000;

        if (distanceMeters > MAX_DISTANCE_METERS) {
            throw new BusinessException(
                    "Deliveryman is too far from destination to complete delivery"
            );
        }
    }
}
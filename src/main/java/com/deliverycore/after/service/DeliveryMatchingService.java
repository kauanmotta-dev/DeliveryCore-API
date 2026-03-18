package com.deliverycore.after.service;

import com.deliverycore.after.model.deliveryLocation.entity.DeliverymanCurrentLocation;
import com.deliverycore.after.model.order.entity.Order;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.model.user.enums.DeliverymanStatus;
import com.deliverycore.after.model.user.enums.UserRole;
import com.deliverycore.after.repository.DeliverymanCurrentLocationRepository;
import com.deliverycore.after.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryMatchingService {

    private final UserRepository userRepository;
    private final DeliverymanCurrentLocationRepository locationRepository;
    private final DistanceService distanceService;
    private final NotificationService notificationService;

    public void findNearbyDeliverymen(Order order) {

        List<User> deliverymen =
                userRepository.findByRoleAndDeliverymanStatus(
                        UserRole.DELIVERYMAN,
                        DeliverymanStatus.ONLINE
                );

        double originLat = order.getOriginAddress().getLatitude();
        double originLon = order.getOriginAddress().getLongitude();

        deliverymen.stream()

                .map(deliveryman -> {

                    DeliverymanCurrentLocation location =
                            locationRepository.findById(deliveryman.getId())
                                    .orElse(null);

                    if (location == null) return null;

                    double distance =
                            distanceService.calculate(
                                    originLat,
                                    originLon,
                                    location.getLatitude(),
                                    location.getLongitude()
                            );

                    return new DeliveryDistance(deliveryman, distance);

                })

                .filter(d -> d != null)

                .sorted(Comparator.comparingDouble(DeliveryDistance::distance))

                .limit(10)

                .forEach(d ->
                        notificationService.notifyNewAvailableOrder(
                                d.deliveryman(),
                                order
                        )
                );
    }

    private record DeliveryDistance(User deliveryman, double distance) {}
}
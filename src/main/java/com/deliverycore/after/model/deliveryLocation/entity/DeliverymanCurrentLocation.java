package com.deliverycore.after.model.deliveryLocation.entity;

import com.deliverycore.after.model.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveryman_current_location")
@Getter
@Setter
public class DeliverymanCurrentLocation {

    @Id
    private Long deliverymanId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "deliveryman_id")
    private User deliveryman;

    private Double latitude;

    private Double longitude;

    private LocalDateTime updatedAt;

}
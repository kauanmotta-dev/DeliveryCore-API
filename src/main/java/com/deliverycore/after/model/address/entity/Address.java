package com.deliverycore.after.model.address.entity;

import com.deliverycore.after.model.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    private String label;

    private String street;

    private String number;

    private String neighborhood;

    private String city;

    private String state;

    private String zipCode;

    private Double latitude;

    private Double longitude;

}
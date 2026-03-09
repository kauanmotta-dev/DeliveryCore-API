package com.douradelivery.after.service;

import com.douradelivery.after.model.address.dto.AddressCreateRequestDTO;
import com.douradelivery.after.model.address.entity.Address;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public Address createAddress(User user, AddressCreateRequestDTO dto) {

        Address address = new Address();

        address.setUser(user);
        address.setLabel(dto.label());
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setNeighborhood(dto.neighborhood());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setZipCode(dto.zipCode());
        address.setLatitude(dto.latitude());
        address.setLongitude(dto.longitude());

        return addressRepository.save(address);
    }

    public List<Address> listUserAddresses(User user) {

        return addressRepository.findByUser(user);
    }
}
package com.douradelivery.after.repository;

import com.douradelivery.after.model.address.entity.Address;
import com.douradelivery.after.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUser(User user);

}
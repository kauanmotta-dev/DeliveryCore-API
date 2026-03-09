package com.douradelivery.after.service;

import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.user.enums.DeliverymanStatus;
import com.douradelivery.after.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliverymanStatusService {

    private final UserRepository userRepository;

    public void goOnline(User user) {

        user.setDeliverymanStatus(DeliverymanStatus.ONLINE);

        userRepository.save(user);
    }

    public void goOffline(User user) {

        user.setDeliverymanStatus(DeliverymanStatus.OFFLINE);

        userRepository.save(user);
    }
}
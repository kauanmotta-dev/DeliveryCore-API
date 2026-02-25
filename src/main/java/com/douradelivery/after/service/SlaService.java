package com.douradelivery.after.service;

import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SlaService {

    private final UserRepository userRepository;

    public void registerClientCancellation(User client) {
        client.setClientPenaltyCount(
                client.getClientPenaltyCount() + 1
        );
        userRepository.save(client);
    }

    public void registerDeliverymanWithdrawal(User deliveryman) {
        deliveryman.setDeliverymanPenaltyCount(
                deliveryman.getDeliverymanPenaltyCount() + 1
        );
        userRepository.save(deliveryman);
    }
}


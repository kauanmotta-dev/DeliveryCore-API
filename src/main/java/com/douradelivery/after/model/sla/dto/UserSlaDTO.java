package com.douradelivery.after.model.sla.dto;

import com.douradelivery.after.model.user.entity.User;

public record UserSlaDTO(
        Long userId,
        String email,
        int clientPenaltyCount,
        int deliverymanPenaltyCount
) {
    public static UserSlaDTO from(User user) {
        return new UserSlaDTO(
                user.getId(),
                user.getEmail(),
                user.getClientPenaltyCount(),
                user.getDeliverymanPenaltyCount()
        );
    }
}

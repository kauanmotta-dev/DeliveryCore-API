package com.douradelivery.after.service;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.audit.enums.AuditLogAction;
import com.douradelivery.after.model.deliverymanVerification.dto.DeliverymanVerificationResponseDTO;
import com.douradelivery.after.model.deliverymanVerification.enums.VerificationStatus;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.deliverymanVerification.dto.DeliverymanVerificationRequestDTO;
import com.douradelivery.after.model.deliverymanVerification.entity.DeliverymanVerification;
import com.douradelivery.after.repository.DeliverymanVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliverymanVerificationService {

    private final DeliverymanVerificationRepository repository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public void requestVerification(
            User user,
            DeliverymanVerificationRequestDTO dto
    ) {

        Optional<DeliverymanVerification> lastVerification =
                repository.findTopByUserOrderByCreatedAtDesc(user);

        if (lastVerification.isPresent()) {

            VerificationStatus status = lastVerification.get().getStatus();

            if (status == VerificationStatus.PENDING ||
                    status == VerificationStatus.UNDER_REVIEW) {

                throw new BusinessException("Verification already in progress");
            }

            if (status == VerificationStatus.APPROVED) {
                throw new BusinessException("User already approved as deliveryman");
            }

            if (status == VerificationStatus.SUSPENDED) {
                throw new BusinessException(
                        "Previous verification was suspended. Please contact support."
                );
            }
        }

        DeliverymanVerification verification = new DeliverymanVerification();

        verification.initialize(user);

        verification.setDocumentCpf(dto.documentCpf());
        verification.setDocumentRg(dto.documentRg());
        verification.setDriverLicense(dto.driverLicense());

        verification.setVehicleType(dto.vehicleType());
        verification.setVehiclePlate(dto.vehiclePlate());

        verification.setSelfieUrl(dto.selfieUrl());
        verification.setDocumentFrontUrl(dto.documentFrontUrl());
        verification.setDocumentBackUrl(dto.documentBackUrl());

        repository.save(verification);

        auditLogService.log(
                AuditLogAction.DELIVERYMAN_VERIFICATION_REQUESTED,
                "DeliverymanVerification",
                verification.getId(),
                user.getId(),
                "User requested deliveryman verification"
        );

        notificationService.notifyDeliverymanVerificationAfterCommit(
                user,
                VerificationStatus.PENDING
        );
    }

    public void approveVerification(
            Long verificationId,
            User admin
    ) {

        DeliverymanVerification verification =
                repository.findById(verificationId)
                        .orElseThrow(() ->
                                new BusinessException("Verification not found"));

        verification.approve(admin);

        User user = verification.getUser();

        user.promoteToDeliveryman();

        auditLogService.log(
                AuditLogAction.DELIVERYMAN_APPROVED,
                "DeliverymanVerification",
                verification.getId(),
                admin.getId(),
                "Admin approved deliveryman"
        );

        notificationService.notifyDeliverymanVerificationAfterCommit(
                user,
                VerificationStatus.APPROVED
        );
    }

    public void rejectVerification(
            Long verificationId,
            User admin
    ) {

        DeliverymanVerification verification =
                repository.findById(verificationId)
                        .orElseThrow(() ->
                                new BusinessException("Verification not found"));

        verification.reject(admin);

        User user = verification.getUser();

        auditLogService.log(
                AuditLogAction.DELIVERYMAN_REJECTED,
                "DeliverymanVerification",
                verification.getId(),
                admin.getId(),
                "Admin rejected verification"
        );

        notificationService.notifyDeliverymanVerificationAfterCommit(
                user,
                VerificationStatus.REJECTED
        );
    }

    public void suspendDeliveryman(
            Long verificationId,
            User admin,
            String reason
    ) {

        DeliverymanVerification verification =
                repository.findById(verificationId)
                        .orElseThrow(() ->
                                new BusinessException("Verification not found"));

        verification.suspend(admin, reason);

        User user = verification.getUser();

        auditLogService.log(
                AuditLogAction.DELIVERYMAN_SUSPENDED,
                "DeliverymanVerification",
                verification.getId(),
                admin.getId(),
                "Admin suspended deliveryman"
        );

        notificationService.notifyDeliverymanVerificationAfterCommit(
                user,
                VerificationStatus.SUSPENDED
        );
    }


    @Transactional(readOnly = true)
    public List<DeliverymanVerificationResponseDTO> listAllVerifications() {

        return repository.findAll()
                .stream()
                .map(v -> new DeliverymanVerificationResponseDTO(
                        v.getId(),
                        v.getUser().getId(),
                        v.getUser().getName(),
                        v.getStatus(),
                        v.getVehicleType(),
                        v.getVehiclePlate(),
                        v.getCreatedAt(),
                        v.getReviewedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public DeliverymanVerificationResponseDTO getVerification(Long id) {

        DeliverymanVerification v = repository.findById(id)
                .orElseThrow(() ->
                        new BusinessException("Verification not found"));

        return new DeliverymanVerificationResponseDTO(
                v.getId(),
                v.getUser().getId(),
                v.getUser().getName(),
                v.getStatus(),
                v.getVehicleType(),
                v.getVehiclePlate(),
                v.getCreatedAt(),
                v.getReviewedAt()
        );
    }
}
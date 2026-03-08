package com.douradelivery.after.model.deliverymanVerification.entity;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.deliverymanVerification.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveryman_verifications")
@Getter
public class DeliverymanVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Setter private String documentCpf;
    @Setter private String documentRg;
    @Setter private String driverLicense;

    @Setter private String vehicleType;
    @Setter private String vehiclePlate;

    @Setter private String selfieUrl;
    @Setter private String documentFrontUrl;
    @Setter private String documentBackUrl;

    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String suspensionReason;



    public void initialize(User user) {
        this.user = user;
        this.status = VerificationStatus.PENDING;
        this.createdAt = LocalDateTime.now();

        validateInvariants();
    }

    private void validateInvariants() {

        if (this.user == null) {
            throw new IllegalStateException(
                    "Verification must always have a user"
            );
        }

        if (this.status == VerificationStatus.APPROVED) {

            if (this.reviewedBy == null) {
                throw new IllegalStateException(
                        "Approved verification must have reviewer"
                );
            }
        }

        if (this.status == VerificationStatus.SUSPENDED) {

            if (this.suspensionReason == null ||
                    this.suspensionReason.isBlank()) {

                throw new IllegalStateException(
                        "Suspended verification must have reason"
                );
            }
        }
    }

    public void ensureApproved() {
        if (this.status != VerificationStatus.APPROVED) {
            throw new BusinessException("Deliveryman is not approved");
        }
    }



    public void startReview(User admin) {

        if (this.status != VerificationStatus.PENDING) {
            throw new BusinessException(
                    "Verification cannot enter review from status " + this.status
            );
        }

        this.status = VerificationStatus.UNDER_REVIEW;
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();

        validateInvariants();
    }

    public void approve(User admin) {

        if (this.status != VerificationStatus.UNDER_REVIEW &&
                this.status != VerificationStatus.SUSPENDED) {

            throw new BusinessException(
                    "Verification cannot be approved from status " + this.status
            );
        }

        this.status = VerificationStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = admin;
    }

    public void reject(User admin) {

        if (this.status != VerificationStatus.UNDER_REVIEW) {
            throw new BusinessException(
                    "Verification cannot be rejected from status " + this.status
            );
        }

        this.status = VerificationStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = admin;
    }

    public void suspend(User admin, String reason) {

        if (this.status != VerificationStatus.APPROVED) {
            throw new BusinessException(
                    "Only approved deliverymen can be suspended"
            );
        }

        this.status = VerificationStatus.SUSPENDED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = admin;
        this.suspensionReason = reason;
    }

}
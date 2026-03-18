package com.deliverycore.after.model.audit.entity;

import com.deliverycore.after.model.audit.enums.AuditLogAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AuditLogAction action;

    private String entityType;

    private Long entityId;

    private Long userId;

    private String details;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime createdAt;

}
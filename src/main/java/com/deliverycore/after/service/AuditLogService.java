package com.deliverycore.after.service;

import com.deliverycore.after.model.audit.entity.AuditLog;
import com.deliverycore.after.model.audit.enums.AuditLogAction;
import com.deliverycore.after.repository.AuditLogRepository;
import com.deliverycore.after.util.TransactionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repository;
    private final HttpServletRequest request;

    public void log(
            AuditLogAction action,
            String entityType,
            Long entityId,
            Long userId,
            String details
    ) {

        TransactionUtils.runAfterCommit(() -> {

            AuditLog log = new AuditLog();

            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setUserId(userId);
            log.setDetails(details);

            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));

            log.setCreatedAt(LocalDateTime.now());

            repository.save(log);

        });
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.service;

import java.util.List;

import com.payter.service.auditlogging.entity.AuditLogging;
import com.payter.service.auditlogging.entity.AuditLogging.EventType;
import com.payter.service.auditlogging.repository.AuditLoggingRepository;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class DefaultAuditLoggingService implements AuditLoggingService {

    private final AuditLoggingRepository repository;

    public DefaultAuditLoggingService(AuditLoggingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void log(EventType eventType, String details) throws Exception {
        AuditLogging log = new AuditLogging();
        log.setEventType(eventType);
        log.setDetails(details);
        repository.writeLogEntry(log);
    }

    @Override
    public List<AuditLogging> getLogs(long afterId) throws Exception {
        return repository.getLogs(afterId);
    }
}
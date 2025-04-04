// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.service;

import com.payter.service.auditlogging.entity.AuditLogging;
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
    public void log(String message) throws Exception {
        AuditLogging log = new AuditLogging();
        log.setMessage(message);
        repository.save(log);
    }
}
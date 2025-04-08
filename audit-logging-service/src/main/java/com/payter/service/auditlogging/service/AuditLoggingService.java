// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.service;

import java.util.List;

import com.payter.service.auditlogging.entity.AuditLogging;
import com.payter.service.auditlogging.entity.AuditLogging.EventType;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface AuditLoggingService {

    void log(EventType eventType, String details) throws Exception;

    List<AuditLogging> getLogs(long afterId) throws Exception;
}
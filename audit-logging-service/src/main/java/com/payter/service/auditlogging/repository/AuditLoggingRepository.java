// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.repository;

import com.payter.service.auditlogging.entity.AuditLogging;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface AuditLoggingRepository {

    void save(AuditLogging log) throws Exception;
}
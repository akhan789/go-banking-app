// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.service;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface AuditLoggingService {

    void log(String message) throws Exception;
}
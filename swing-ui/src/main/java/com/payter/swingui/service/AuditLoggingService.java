// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import java.util.List;

import com.payter.swingui.client.AuditLoggingHttpClient;
import com.payter.swingui.model.AuditLoggingEntry;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingService {

    private AuditLoggingHttpClient auditClient = new AuditLoggingHttpClient();

    public List<AuditLoggingEntry> getAuditLogsAfter(long lastLogId) {
        return auditClient.getAuditLogsAfter(lastLogId);
    }
}
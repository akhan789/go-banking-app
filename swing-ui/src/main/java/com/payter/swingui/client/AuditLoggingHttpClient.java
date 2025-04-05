// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.payter.common.util.ConfigUtil;
import com.payter.swingui.model.AuditLoggingEntry;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingHttpClient extends AbstractHttpClient {

    private static final String ENDPOINT = ConfigUtil.loadProperty("service.gateway.auditLogging.endpoint",
            "/auditlogging");

    public AuditLoggingHttpClient() {
    }

    public List<AuditLoggingEntry> getAuditLogsAfter(long lastLogId) {
        try {
            String endpoint = ENDPOINT + "/logs?after=" + lastLogId;
            List<AuditLoggingEntry> logs = sendGetRequest(endpoint, new TypeReference<List<AuditLoggingEntry>>() {
            });
            return logs != null ? logs : Collections.emptyList();
        }
        catch(Exception e) {
            return Collections.emptyList();
        }
    }
}
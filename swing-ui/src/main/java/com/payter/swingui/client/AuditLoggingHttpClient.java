// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.payter.common.dto.auditlogging.AuditLoggingRequestDTO;
import com.payter.common.util.ConfigUtil;
import com.payter.swingui.model.AuditLoggingEntry;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingHttpClient extends AbstractHttpClient {

    private static final String ENDPOINT = ConfigUtil.loadProperty("service.gateway.auditLogging.endpoint",
            "/auditlogging");
    private boolean lastRequestFailed = false; // Track failure state to reduce logging

    public AuditLoggingHttpClient() {
        super();
    }

    public List<AuditLoggingEntry> getAuditLogsAfter(long lastLogId) {
        try {
            String endpoint = ENDPOINT + "/logs?after=" + lastLogId;
            List<AuditLoggingRequestDTO> dtos = sendGetRequest(endpoint,
                    new TypeReference<List<AuditLoggingRequestDTO>>() {
                    });
            lastRequestFailed = false; // Reset on success
            return dtos != null ? dtos.stream().map(this::mapToAuditLoggingEntry).collect(Collectors.toList())
                    : Collections.emptyList();
        }
        catch(IOException e) {
            if(!lastRequestFailed) {
                System.err.println("Failed to get audit logs: Service offline or unreachable - " + e.getMessage());
                lastRequestFailed = true;
            }
            return null;
        }
        catch(Exception e) {
            // Check if it's an HTTP error (e.g., 500)
            if(e.getMessage() != null && e.getMessage().contains("status 500")) {
                if(!lastRequestFailed) {
                    System.err.println("Failed to get audit logs: Server error (HTTP 500) - " + e.getMessage());
                    lastRequestFailed = true;
                }
            }
            else if(!lastRequestFailed) {
                System.err.println("Failed to get audit logs: Unexpected error - " + e.getMessage());
                lastRequestFailed = true;
            }
            return null;
        }
    }

    private AuditLoggingEntry mapToAuditLoggingEntry(AuditLoggingRequestDTO dto) {
        if(dto == null) {
            return null;
        }
        return new AuditLoggingEntry(dto.getId(), dto.getEventType(), dto.getDetails(), dto.getTimestamp());
    }
}
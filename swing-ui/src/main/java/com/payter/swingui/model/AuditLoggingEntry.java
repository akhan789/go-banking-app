// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.model;

import java.time.LocalDateTime;

import com.payter.common.dto.auditlogging.AuditLoggingRequestDTO;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingEntry extends AuditLoggingRequestDTO {

    public AuditLoggingEntry() {
    }

    public AuditLoggingEntry(long id, EventType eventType, String details, LocalDateTime timestamp) {
        super(id, eventType, details, timestamp);
    }

    @Override
    public String toString() {
        return "[" + (getTimestamp() != null ? getTimestamp().toString() : "N/A") + ": " + getEventType().name() + "] "
                + getDetails();
    }
}
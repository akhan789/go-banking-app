// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.model;

import java.time.LocalDateTime;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingEntry {

    private long id;
    private String eventType;
    private String details;
    private LocalDateTime timestamp;

    public AuditLoggingEntry() {
    }

    public AuditLoggingEntry(String eventType, String details, LocalDateTime timestamp) {
        this.id = -1;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = timestamp;
    }

    public AuditLoggingEntry(long id, String eventType, String details, LocalDateTime timestamp) {
        this.id = id;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%d] [%s] %s: %s", id, timestamp, eventType, details);
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.dto.auditlogging;

import java.time.LocalDateTime;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingRequestDTO {

    private long id;
    private EventType eventType;
    private String details;
    private LocalDateTime timestamp;

    public enum EventType {
        INFO, WARNING, ERROR, CREATE, READ, UPDATE, DELETE
    }

    public AuditLoggingRequestDTO() {
    }

    public AuditLoggingRequestDTO(EventType eventType, String details) {
        this.id = -1L;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public AuditLoggingRequestDTO(EventType eventType, String details, LocalDateTime timestamp) {
        this.id = -1L;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = timestamp;
    }

    public AuditLoggingRequestDTO(long id, EventType eventType, String details, LocalDateTime timestamp) {
        this.id = id;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.dto.auditlogging;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingRequestDTO {

    private String message;

    public AuditLoggingRequestDTO() {
    }

    public AuditLoggingRequestDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
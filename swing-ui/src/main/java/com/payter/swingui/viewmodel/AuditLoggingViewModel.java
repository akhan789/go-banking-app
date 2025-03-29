// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.swingui.viewmodel;

import javax.swing.JTextArea;

/**
 * 
 * 
 * @author AK
 * @since 0.0.1_SNAPSHOT
 * @created 28 Mar 2025
 * @version $Revision$
 */
public class AuditLoggingViewModel {

    private JTextArea auditLogArea;

    public void setAuditLogArea(JTextArea auditLogArea) {
        this.auditLogArea = auditLogArea;
    }

    public void logTransaction(String transactionDetails) {
        // Add a log entry in the audit log area
        auditLogArea.append(transactionDetails + "\n");
    }
}
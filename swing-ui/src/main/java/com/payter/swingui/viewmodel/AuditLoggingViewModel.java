// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.payter.swingui.model.AuditLoggingEntry;
import com.payter.swingui.service.AuditLoggingService;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingViewModel {

    private JTextArea auditLogArea;
    private final AuditLoggingService auditService;
    private long lastLogId = -1;
    private boolean serviceOfflineNotified = false;
    private boolean serverErrorNotified = false;

    public AuditLoggingViewModel() {
        this.auditService = new AuditLoggingService();
    }

    public void setAuditLogArea(JTextArea auditLogArea) {
        this.auditLogArea = auditLogArea;
    }

    public void updateAuditLogs() {
        List<AuditLoggingEntry> logs = auditService.getAuditLogsAfter(lastLogId);
        if(logs != null && !logs.isEmpty()) {
            StringBuilder logText = new StringBuilder();
            for(AuditLoggingEntry log : logs) {
                logText.append(log.toString()).append("\n");
                if(log.getId() != null && log.getId() > lastLogId) {
                    lastLogId = log.getId();
                }
            }
            SwingUtilities.invokeLater(() -> {
                auditLogArea.append(logText.toString());
                auditLogArea.setCaretPosition(auditLogArea.getDocument().getLength());
                serviceOfflineNotified = false;
                serverErrorNotified = false; // Reset both flags on success
            });
        }
        else if(logs == null) {
            SwingUtilities.invokeLater(() -> {
                if(!serviceOfflineNotified && !serverErrorNotified) {
                    // First failure; determine the type
                    auditLogArea.append("[WARNING] Audit logging service issue detected.\n");
                    serviceOfflineNotified = true; // Default to offline; adjusted in View if server error
                }
                auditLogArea.setCaretPosition(auditLogArea.getDocument().getLength());
            });
        }
    }
}
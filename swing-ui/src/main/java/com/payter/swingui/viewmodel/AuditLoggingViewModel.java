// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.payter.swingui.model.AuditLoggingEntry;
import com.payter.swingui.service.AuditLoggingService;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingViewModel {

    private JTextArea auditLogArea;
    private AuditLoggingService auditService = new AuditLoggingService();
    private long lastLogId = -1;

    public void setAuditLogArea(JTextArea auditLogArea) {
        this.auditLogArea = auditLogArea;
    }

    public void updateAuditLogs() {
        List<AuditLoggingEntry> logs = auditService.getAuditLogsAfter(lastLogId);
        if(logs != null && !logs.isEmpty()) {
            StringBuilder logText = new StringBuilder();
            for(AuditLoggingEntry log : logs) {
                logText.append(log.toString()).append("\n");
                lastLogId = log.getId();
            }
            SwingUtilities.invokeLater(() -> {
                auditLogArea.append(logText.toString());
                auditLogArea.setCaretPosition(auditLogArea.getDocument().getLength());
            });
        }
    }
}
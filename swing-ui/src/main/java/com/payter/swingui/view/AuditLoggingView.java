// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.view;

import java.awt.BorderLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.payter.common.util.ConfigUtil;
import com.payter.swingui.viewmodel.AuditLoggingViewModel;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingView extends AbstractView {

    private static final long serialVersionUID = 920288068313477109L;

    private JTextArea logArea = new JTextArea(8, 30);
    private final AuditLoggingViewModel auditLoggingVM;
    private ScheduledExecutorService scheduler;
    private boolean isServiceOffline = false;
    private long lastPollTime = 0;
    private static final boolean POLLING_ENABLED = Boolean
            .valueOf(ConfigUtil.loadProperty("service.auditlogging.polling.enabled", "true")).booleanValue();
    private static final long MIN_POLL_INTERVAL_MS = Long
            .valueOf(ConfigUtil.loadProperty("service.auditlogging.polling.milliseconds", "5000"));

    public AuditLoggingView(AuditLoggingViewModel auditLogVM) {
        this.auditLoggingVM = auditLogVM;
        setLayout(new BorderLayout());
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        this.auditLoggingVM.setAuditLogArea(logArea);
        add(scrollPane, BorderLayout.CENTER);
        if(POLLING_ENABLED) {
            startPolling();
        }
    }

    private void startPolling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        int pollIntervalSeconds = Integer
                .parseInt(ConfigUtil.loadProperty("service.auditlogging.polling.seconds", "5"));
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if(isServiceOffline && (currentTime - lastPollTime < MIN_POLL_INTERVAL_MS)) {
                return; // Throttle polling when service is offline or in error state
            }
            try {
                auditLoggingVM.updateAuditLogs();
                if(isServiceOffline) {
                    isServiceOffline = false;
                    SwingUtilities.invokeLater(() -> logArea.append("[INFO] Audit logging service restored.\n"));
                }
            }
            catch(Exception e) {
                if(!isServiceOffline) {
                    isServiceOffline = true;
                    lastPollTime = currentTime;
                    String errorMsg = e.getMessage() != null && e.getMessage().contains("status 500")
                            ? "[ERROR] Audit logging service encountered a server error (HTTP 500).\n"
                            : "[WARNING] Audit logging service is offline or unreachable.\n";
                    SwingUtilities.invokeLater(() -> logArea.append(errorMsg));
                }
            }
        }, 0, pollIntervalSeconds, TimeUnit.SECONDS);
    }

    public void cleanup() {
        if(scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
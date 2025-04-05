// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.view;

import java.awt.BorderLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
    private AuditLoggingViewModel auditLoggingVM;
    private ScheduledExecutorService scheduler;

    public AuditLoggingView(AuditLoggingViewModel auditLogVM) {
        this.auditLoggingVM = auditLogVM;
        setLayout(new BorderLayout());

        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        this.auditLoggingVM.setAuditLogArea(logArea);

        add(scrollPane, BorderLayout.CENTER);

        startPolling();
    }

    private void startPolling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                auditLoggingVM.updateAuditLogs();
            }
            catch(Exception e) {
                logArea.append(e.getMessage() + "\n");
            }
        }, 0, Integer.valueOf(ConfigUtil.loadProperty("service.auditlogging.polling.seconds", "5")), TimeUnit.SECONDS);
    }

    public void cleanup() {
        if(scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
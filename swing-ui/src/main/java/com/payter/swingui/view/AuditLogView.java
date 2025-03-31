// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.payter.swingui.viewmodel.AuditLoggingViewModel;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLogView extends AbstractView {

    private static final long serialVersionUID = 920288068313477109L;

    private JTextArea logArea = new JTextArea(8, 30);

    private AuditLoggingViewModel auditLogVM;

    public AuditLogView(AuditLoggingViewModel auditLogVM) {
        this.auditLogVM = auditLogVM;
        setLayout(new BorderLayout());

        // Log Area
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        this.auditLogVM.setAuditLogArea(logArea);

        add(scrollPane, BorderLayout.CENTER);
    }
}
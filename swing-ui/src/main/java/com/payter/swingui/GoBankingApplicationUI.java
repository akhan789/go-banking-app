// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.payter.common.util.ConfigUtil;
import com.payter.swingui.view.AccountManagementView;
import com.payter.swingui.view.AuditLoggingView;
import com.payter.swingui.view.BalanceOperationsView;
import com.payter.swingui.view.InterestManagementView;
import com.payter.swingui.viewmodel.AccountManagementViewModel;
import com.payter.swingui.viewmodel.AuditLoggingViewModel;
import com.payter.swingui.viewmodel.BalanceOperationsViewModel;
import com.payter.swingui.viewmodel.InterestManagementViewModel;

/**
 * Main UI frame for the Go Banking Application.
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class GoBankingApplicationUI extends JFrame {

    private static final long serialVersionUID = -163076521878436580L;
    private AuditLoggingView auditLoggingView;

    public GoBankingApplicationUI() {
        setTitle(ConfigUtil.loadProperty("ui.title", "Go Banking App"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        createMenuBar();
        createUI();
        pack();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            cleanup();
            System.exit(0);
        });
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void createUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Account Management", new AccountManagementView(new AccountManagementViewModel()));
        tabbedPane.addTab("Balance Operations", new BalanceOperationsView(new BalanceOperationsViewModel()));
        tabbedPane.addTab("Interest Management", new InterestManagementView(new InterestManagementViewModel()));
        auditLoggingView = new AuditLoggingView(new AuditLoggingViewModel());
        tabbedPane.addTab("Audit Logging", auditLoggingView);
        add(tabbedPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                cleanup();
            }
        });
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, "Go Banking App\nVersion 25.0.0.0\n\nDeveloped by Abid Khan.", "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cleanup() {
        if(auditLoggingView != null) {
            auditLoggingView.cleanup();
        }
    }
}
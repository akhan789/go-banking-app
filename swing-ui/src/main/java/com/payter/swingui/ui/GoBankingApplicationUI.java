// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.ui;

import java.awt.BorderLayout;
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
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class GoBankingApplicationUI extends JFrame {

    private static final long serialVersionUID = -163076521878436580L;

    public GoBankingApplicationUI() {
        setTitle(ConfigUtil.loadProperty("ui.title", "Go Banking App"));
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        createMenuBar();
        createUI();
    }

    // Create a simple menu bar with File, Exit, and Help options
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu with Exit option
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Help menu with About option
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    // Create UI with tabs for each functionality
    private void createUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Account Management", new AccountManagementView(new AccountManagementViewModel()));
        tabbedPane.addTab("Balance Operations", new BalanceOperationsView(new BalanceOperationsViewModel()));
        tabbedPane.addTab("Interest Management", new InterestManagementView(new InterestManagementViewModel()));
        AuditLoggingView auditLoggingView = new AuditLoggingView(new AuditLoggingViewModel());
        tabbedPane.addTab("Audit Logging", auditLoggingView);
        add(tabbedPane, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                auditLoggingView.cleanup();
            }
        });
    }

    // Method to show the About dialog
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, "Go Banking App\nVersion 0.0.1-SNAPSHOT\n\nDeveloped by Abid Khan.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
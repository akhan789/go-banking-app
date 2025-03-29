// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.payter.swingui.view.AccountView;
import com.payter.swingui.view.AuditLogView;
import com.payter.swingui.view.BalanceView;
import com.payter.swingui.view.InterestView;
import com.payter.swingui.viewmodel.AccountViewModel;
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

    private AccountViewModel accountVM = new AccountViewModel();
    private BalanceOperationsViewModel balanceOpsVM = new BalanceOperationsViewModel();
    private InterestManagementViewModel interestVM = new InterestManagementViewModel();
    private AuditLoggingViewModel auditLogVM = new AuditLoggingViewModel();

    public GoBankingApplicationUI() {
        setTitle("Go Banking App");
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
        tabbedPane.addTab("Account Management", new AccountView(accountVM));
        tabbedPane.addTab("Balance Operations", new BalanceView(balanceOpsVM));
        tabbedPane.addTab("Interest Management", new InterestView(interestVM));
        tabbedPane.addTab("Audit Logging", new AuditLogView(auditLogVM));
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Method to show the About dialog
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, "Go Banking App\nVersion 0.0.1-SNAPSHOT\n\nDeveloped by Abid Khan.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
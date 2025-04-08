// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import com.payter.swingui.model.Account;
import com.payter.swingui.viewmodel.AccountManagementViewModel;
import com.payter.swingui.viewmodel.AccountViewModelException;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementView extends AbstractView {

    private static final long serialVersionUID = -7079536813258363853L;

    private JTextField nameField = new JTextField(15);
    private JTextField depositField = new JTextField(15);
    private JComboBox<String> currencyCombo = new JComboBox<>(new String[] {
            "GBP", "EUR", "JPY"
    });
    private JButton createBtn = new JButton("Create");

    private JTextField idField = new JTextField(15);
    private JButton suspendBtn = new JButton("Suspend");
    private JButton reactivateBtn = new JButton("Reactivate");
    private JButton closeBtn = new JButton("Close");

    private final AccountManagementViewModel accountVM;

    public AccountManagementView(AccountManagementViewModel accountVM) {
        this.accountVM = accountVM;
        setLayout(new BorderLayout());

        JPanel leftPanel = createAccountPanel();
        JPanel rightPanel = createOperationsPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setEnabled(false);
        add(splitPane, BorderLayout.CENTER);

        setupListeners();
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Account Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Deposit:"), gbc);
        gbc.gridx = 1;
        panel.add(depositField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Currency:"), gbc);
        gbc.gridx = 1;
        panel.add(currencyCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(createBtn, gbc);

        return panel;
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Account ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(suspendBtn, gbc);
        gbc.gridy = 2;
        panel.add(reactivateBtn, gbc);
        gbc.gridy = 3;
        panel.add(closeBtn, gbc);

        return panel;
    }

    private void setupListeners() {
        createBtn.addActionListener(ae -> {
            try {
                Account account = accountVM.createAccount(nameField.getText(), depositField.getText(),
                        (String) currencyCombo.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Account created: " + account.getAccountId(), "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                depositField.setText("");
            }
            catch(AccountViewModelException e) {
                showError(e.getMessage());
            }
        });

        suspendBtn.addActionListener(ae -> {
            try {
                Account account = accountVM.suspendAccount(idField.getText());
                JOptionPane.showMessageDialog(this, "Account suspended: " + account.getAccountId(), "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch(AccountViewModelException e) {
                showError(e.getMessage());
            }
        });

        reactivateBtn.addActionListener(ae -> {
            try {
                Account account = accountVM.reactivateAccount(idField.getText());
                JOptionPane.showMessageDialog(this, "Account reactivated: " + account.getAccountId(), "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch(AccountViewModelException e) {
                showError(e.getMessage());
            }
        });

        closeBtn.addActionListener(ae -> {
            try {
                Account account = accountVM.closeAccount(idField.getText());
                JOptionPane.showMessageDialog(this, "Account closed: " + account.getAccountId(), "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch(AccountViewModelException e) {
                showError(e.getMessage());
            }
        });
    }
}
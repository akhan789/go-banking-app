// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.swingui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.payter.swingui.viewmodel.BalanceOperationsViewModel;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceView extends JPanel {

    private static final long serialVersionUID = -2993345144041076375L;

    private JTextField accountIdField = new JTextField(15);
    private JTextField toAccountIdField = new JTextField(15);
    private JTextField amountField = new JTextField(10);
    private JLabel balanceLabel = new JLabel("$0.00");

    private JButton creditBtn = new JButton("Credit");
    private JButton debitBtn = new JButton("Debit");
    private JButton transferBtn = new JButton("Transfer");

    private BalanceOperationsViewModel balanceOpsVM;

    public BalanceView(BalanceOperationsViewModel balanceOpsVM) {
        this.balanceOpsVM = balanceOpsVM;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Account ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("From Account ID:"), gbc);
        gbc.gridx = 1;
        add(accountIdField, gbc);

        // To Account ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("To Account ID:"), gbc);
        gbc.gridx = 1;
        add(toAccountIdField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        add(amountField, gbc);

        // Balance
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Balance:"), gbc);
        gbc.gridx = 1;
        add(balanceLabel, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(creditBtn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(debitBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(transferBtn, gbc);

        // Action Listeners
        creditBtn.addActionListener(e -> {
            String accountId = accountIdField.getText();
            String amountText = amountField.getText();
            if(isValidAmount(amountText)) {
                double amount = Double.parseDouble(amountText);
                this.balanceOpsVM.processCreditTransaction(accountId, amount);
            }
        });

        debitBtn.addActionListener(e -> {
            String accountId = accountIdField.getText();
            String amountText = amountField.getText();
            if(isValidAmount(amountText)) {
                double amount = Double.parseDouble(amountText);
                this.balanceOpsVM.processDebitTransaction(accountId, amount);
            }
        });

        transferBtn.addActionListener(e -> {
            String fromAccountId = accountIdField.getText();
            String toAccountId = toAccountIdField.getText();
            String amountText = amountField.getText();
            if(isValidAmount(amountText)) {
                double amount = Double.parseDouble(amountText);
                this.balanceOpsVM.processTransferTransaction(fromAccountId, toAccountId, amount);
            }
        });
    }

    // Helper method for validating the amount text input
    private boolean isValidAmount(String amountText) {
        try {
            Double.parseDouble(amountText); // This will throw an exception if the input is not a valid number
            return true;
        }
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
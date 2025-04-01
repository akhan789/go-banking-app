// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import com.payter.swingui.viewmodel.BalanceOperationsViewModel;
import com.payter.swingui.viewmodel.BalanceOperationsViewModelException;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsView extends AbstractView {

    private static final long serialVersionUID = -5264785928441682973L;

    private JTextField accountIdField = new JTextField(15);
    private JLabel balanceLabel = new JLabel("$0.00");
    private JTextField toAccountIdField = new JTextField(15);
    private JTextField amountField = new JTextField(10);

    private JButton creditBtn = new JButton("Credit");
    private JButton debitBtn = new JButton("Debit");
    private JButton transferBtn = new JButton("Transfer");

    private BalanceOperationsViewModel balanceOpsVM;

    public BalanceOperationsView(BalanceOperationsViewModel balanceOpsVM) {
        this.balanceOpsVM = balanceOpsVM;
        setLayout(new BorderLayout());

        JPanel balancePanel = createBalancePanel();
        JPanel transactionPanel = createTransactionPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, balancePanel, transactionPanel);
        splitPane.setResizeWeight(0.3); // 30% left for account info, 70% right for actions
        splitPane.setEnabled(false);
        add(splitPane, BorderLayout.CENTER);

        setupListeners();
    }

    private JPanel createBalancePanel() {
        JPanel balancePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Account ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        balancePanel.add(new JLabel("Account ID:"), gbc);
        gbc.gridx = 1;
        balancePanel.add(accountIdField, gbc);

        // Balance
        gbc.gridx = 0;
        gbc.gridy = 1;
        balancePanel.add(new JLabel("Balance:"), gbc);
        gbc.gridx = 1;
        balancePanel.add(balanceLabel, gbc);

        return balancePanel;
    }

    private JPanel createTransactionPanel() {
        JPanel transactionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // To Account ID (only for transfer)
        gbc.gridx = 0;
        gbc.gridy = 0;
        transactionPanel.add(new JLabel("To Account ID:"), gbc);
        gbc.gridx = 1;
        transactionPanel.add(toAccountIdField, gbc);

        // Amount Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        transactionPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        transactionPanel.add(amountField, gbc);

        // Credit and Debit buttons (centered)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        transactionPanel.add(creditBtn, gbc);
        gbc.gridy = 3;
        transactionPanel.add(debitBtn, gbc);

        // Transfer button (below credit/debit)
        gbc.gridy = 4;
        transactionPanel.add(transferBtn, gbc);

        return transactionPanel;
    }

    private void setupListeners() {
        accountIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent fe) {
                double balance = balanceOpsVM.getBalance(accountIdField.getText());
                balanceLabel.setText("$" + new DecimalFormat("0.00").format(balance));
            }
        });

        creditBtn.addActionListener(ae -> {
            try {
                balanceOpsVM.credit(accountIdField.getText(), amountField.getText());
            }
            catch(BalanceOperationsViewModelException e) {
                showError(e.getMessage());
            }
        });

        debitBtn.addActionListener(ae -> {
            try {
                balanceOpsVM.debit(accountIdField.getText(), amountField.getText());
            }
            catch(BalanceOperationsViewModelException e) {
                showError(e.getMessage());
            }
        });

        transferBtn.addActionListener(ae -> {
            try {
                balanceOpsVM.transfer(accountIdField.getText(), toAccountIdField.getText(), amountField.getText());
            }
            catch(BalanceOperationsViewModelException e) {
                showError(e.getMessage());
            }
        });
    }
}
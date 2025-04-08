// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.payter.swingui.model.InterestCalculationFrequency;
import com.payter.swingui.model.InterestRate;
import com.payter.swingui.viewmodel.InterestManagementViewModel;
import com.payter.swingui.viewmodel.InterestManagementViewModelException;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementView extends AbstractView {

    private static final long serialVersionUID = -455612526050486428L;

    private JTextField rateField = new JTextField(10);
    private JComboBox<String> calculationFrequencyCombo = new JComboBox<>(new String[] {
            "DAILY", "WEEKLY", "MONTHLY"
    });
    private JButton setRateBtn = new JButton("Set Rate");
    private JButton setFrequencyBtn = new JButton("Set Frequency");
    private JButton applyBtn = new JButton("Apply Interest");
    private JButton skipTimeBtn = new JButton("Skip Time");

    private final InterestManagementViewModel interestVM;

    public InterestManagementView(InterestManagementViewModel interestVM) {
        this.interestVM = interestVM;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Global Daily Rate:"), gbc);
        gbc.gridx = 1;
        add(rateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Calculation Frequency:"), gbc);
        gbc.gridx = 1;
        add(calculationFrequencyCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(setRateBtn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(setFrequencyBtn, gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        add(applyBtn, gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        add(skipTimeBtn, gbc);

        loadGlobalDailyRate();
        loadCalculationFrequency();
        setupListeners();
    }

    private void setupListeners() {
        setRateBtn.addActionListener(ae -> {
            String rateText = rateField.getText();
            try {
                interestVM.setInterestRate(rateText);
                JOptionPane.showMessageDialog(this, "Interest rate set to " + rateText + "%", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadGlobalDailyRate();
            }
            catch(InterestManagementViewModelException e) {
                showError(e.getMessage());
            }
        });

        setFrequencyBtn.addActionListener(ae -> {
            String frequency = (String) calculationFrequencyCombo.getSelectedItem();
            try {
                interestVM.setCalculationFrequency(frequency);
                JOptionPane.showMessageDialog(this, "Calculation frequency set to " + frequency, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch(InterestManagementViewModelException e) {
                showError(e.getMessage());
            }
        });

        applyBtn.addActionListener(ae -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "Would you like to force apply the interest rate to all accounts immediately?",
                    "Confirm Interest Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(response == JOptionPane.YES_OPTION) {
                interestVM.applyInterest(true);
                JOptionPane.showMessageDialog(this, "Interest forced applied", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        skipTimeBtn.addActionListener(ae -> {
            String periodsStr = JOptionPane.showInputDialog(this, "Enter the number of periods to skip (based on "
                    + calculationFrequencyCombo.getSelectedItem() + "):", "Skip Time", JOptionPane.PLAIN_MESSAGE);
            if(periodsStr != null && !periodsStr.trim().isEmpty()) {
                try {
                    int periods = Integer.parseInt(periodsStr.trim());
                    if(periods <= 0) {
                        showError("Number of periods must be positive.");
                        return;
                    }
                    String frequency = (String) calculationFrequencyCombo.getSelectedItem();
                    interestVM.skipTime(periods, frequency);
                    JOptionPane.showMessageDialog(this,
                            "Time skipped by " + periods + " " + frequency.toLowerCase() + " periods", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                catch(NumberFormatException e) {
                    showError("Please enter a valid number of periods.");
                }
                catch(InterestManagementViewModelException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void loadGlobalDailyRate() {
        try {
            InterestRate rate = interestVM.getGlobalDailyRate();
            rateField.setText(new DecimalFormat("0.00").format(rate.getRate()));
        }
        catch(InterestManagementViewModelException e) {
            showError(e.getMessage());
            rateField.setText(new DecimalFormat("0.00").format(0.0));
        }
    }

    private void loadCalculationFrequency() {
        try {
            InterestCalculationFrequency frequency = interestVM.getCalculationFrequency();
            calculationFrequencyCombo.setSelectedItem(frequency.getCalculationFrequency());
        }
        catch(InterestManagementViewModelException e) {
            showError(e.getMessage());
            calculationFrequencyCombo.setSelectedItem("MONTHLY");
        }
    }
}
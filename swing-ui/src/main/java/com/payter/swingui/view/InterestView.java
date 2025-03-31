// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.swingui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.payter.swingui.viewmodel.InterestManagementViewModel;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestView extends AbstractView {

    private static final long serialVersionUID = -455612526050486428L;

    private JTextField rateField = new JTextField(10);

    private JButton setBtn = new JButton("Set Rate");
    private JButton applyBtn = new JButton("Apply Interest");

    private InterestManagementViewModel interestVM;

    public InterestView(InterestManagementViewModel interestVM) {
        this.interestVM = interestVM;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Global Interest Rate
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Global Rate:"), gbc);
        gbc.gridx = 1;
        add(rateField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(setBtn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(applyBtn, gbc);

        // Action Listeners
        setBtn.addActionListener(e -> {
            String rateText = rateField.getText();
            if(isValidRate(rateText)) {
                double rate = Double.parseDouble(rateText);
                this.interestVM.setInterestRate(rate);
            }
        });

        applyBtn.addActionListener(e -> this.interestVM.applyInterest());
    }

    private boolean isValidRate(String rateText) {
        try {
            double rate = Double.parseDouble(rateText);
            if(rate < 0) {
                JOptionPane.showMessageDialog(this, "Rate must be a positive number.", "Invalid Rate",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid rate.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
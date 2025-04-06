// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.model.InterestFrequency;
import com.payter.swingui.model.InterestRate;
import com.payter.swingui.service.InterestManagementService;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementViewModel {

    private final InterestManagementService interestManagementService;

    public InterestManagementViewModel() {
        this.interestManagementService = new InterestManagementService();
    }

    public void setInterestRate(String rateText) throws InterestManagementViewModelException {
        double rate = validateRate(rateText);
        interestManagementService.setInterestRate(rate);
        System.out.println("Global interest rate set to: " + rate);
    }

    public InterestRate getGlobalDailyRate() throws InterestManagementViewModelException {
        InterestRate rate = interestManagementService.getGlobalDailyRate();
        if(rate != null) {
            return rate;
        }
        else {
            throw new InterestManagementViewModelException("Failed to retrieve global daily rate.");
        }
    }

    public void setCalculationFrequency(String frequency) throws InterestManagementViewModelException {
        validateFrequency(frequency);
        interestManagementService.setCalculationFrequency(frequency);
        System.out.println("Calculation frequency set to: " + frequency);
    }

    public InterestFrequency getCalculationFrequency() throws InterestManagementViewModelException {
        InterestFrequency frequency = interestManagementService.getCalculationFrequency();
        if(frequency != null) {
            return frequency;
        }
        else {
            throw new InterestManagementViewModelException("Failed to retrieve calculation frequency.");
        }
    }

    public void applyInterest(boolean force) {
        interestManagementService.applyInterest(force);
        System.out.println("Interest applied successfully via service if applicable");
    }

    public void skipTime(int periods, String frequency) throws InterestManagementViewModelException {
        validateFrequency(frequency);
        int periodsToSkip = calculatePeriodsToSkip(periods, frequency);
        interestManagementService.skipTime(periodsToSkip);
        System.out.println("Skipped " + periods + " " + frequency + " periods (" + periodsToSkip + " days)");
    }

    private double validateRate(String rateText) throws InterestManagementViewModelException {
        if(rateText == null || rateText.trim().isEmpty()) {
            throw new InterestManagementViewModelException("Rate cannot be null or empty.");
        }
        double rate;
        try {
            rate = Double.parseDouble(rateText);
        }
        catch(NumberFormatException e) {
            throw new InterestManagementViewModelException("Please enter a valid rate.");
        }
        if(rate < 0) {
            throw new InterestManagementViewModelException("Rate must be a positive number.");
        }
        return rate;
    }

    private void validateFrequency(String frequency) throws InterestManagementViewModelException {
        if(frequency == null || frequency.trim().isEmpty()) {
            throw new InterestManagementViewModelException("Frequency cannot be null or empty.");
        }
        if(!"DAILY|WEEKLY|MONTHLY".contains(frequency.toUpperCase())) {
            throw new InterestManagementViewModelException("Invalid frequency. Use DAILY, WEEKLY, or MONTHLY.");
        }
    }

    private int calculatePeriodsToSkip(int periods, String frequency) throws InterestManagementViewModelException {
        if(periods < 0) {
            throw new InterestManagementViewModelException("Periods to skip must be non-negative.");
        }
        switch(frequency.toUpperCase()) {
            case "DAILY":
                return periods;
            case "WEEKLY":
                return periods * 7;
            case "MONTHLY":
                return periods * 30;
            default:
                throw new InterestManagementViewModelException("Unexpected frequency: " + frequency);
        }
    }
}
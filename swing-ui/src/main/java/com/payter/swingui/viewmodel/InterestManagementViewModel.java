// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.service.InterestManagementService;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementViewModel {

    private InterestManagementService interestManagementService = new InterestManagementService();

    public void setInterestRate(String rateText) throws InterestManagementViewModelException {
        double rate = validateRate(rateText);
        interestManagementService.setInterestRate(rate);
        System.out.println("Global interest rate set to: " + rate);
    }

    public double getGlobalDailyRate() throws InterestManagementViewModelException {
        try {
            return interestManagementService.getGlobalDailyRate();
        }
        catch(Exception e) {
            throw new InterestManagementViewModelException(e);
        }
    }

    public void setCalculationFrequency(String frequency) throws InterestManagementViewModelException {
        validateFrequency(frequency);
        interestManagementService.setCalculationFrequency(frequency);
        System.out.println("Calculation frequency set to: " + frequency);
    }

    public String getCalculationFrequency() throws InterestManagementViewModelException {
        try {
            return interestManagementService.getCalculationFrequency();
        }
        catch(Exception e) {
            throw new InterestManagementViewModelException(e);
        }
    }

    public void applyInterest(boolean force) throws InterestManagementViewModelException {
        try {
            interestManagementService.applyInterest(force);
            System.out.println("Interest applied successfully via service if applicable");
        }
        catch(Exception e) {
            throw new InterestManagementViewModelException(e);
        }
    }

    public void skipTime(int periods, String frequency) throws InterestManagementViewModelException {
        validateFrequency(frequency);
        int periodsToSkip = calculatePeriodsToSkip(periods, frequency);
        try {
            interestManagementService.skipTime(periodsToSkip);
        }
        catch(Exception e) {
            throw new InterestManagementViewModelException("Failed to skip time: " + e.getMessage());
        }
    }


    private double validateRate(String rateText) throws InterestManagementViewModelException {
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
        if(frequency == null || frequency.isEmpty()) {
            throw new InterestManagementViewModelException("Frequency cannot be null or empty.");
        }
        if(!"DAILY|WEEKLY|MONTHLY".contains(frequency.toUpperCase())) {
            throw new InterestManagementViewModelException("Invalid frequency. Use DAILY, WEEKLY, or MONTHLY.");
        }
    }

    private int calculatePeriodsToSkip(int periods, String frequency) {
        switch(frequency.toUpperCase()) {
            case "DAILY":
                return periods;
            case "WEEKLY":
                return periods * 7;
            case "MONTHLY":
                return periods * 30;
            default:
                // Fallback to daily
                return periods;
        }
    }
}
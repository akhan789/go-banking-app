// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import com.payter.swingui.client.InterestManagementHttpClient;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementService {

    private InterestManagementHttpClient interestManagementHttpClient;

    public InterestManagementService() {
        this.interestManagementHttpClient = new InterestManagementHttpClient();
    }

    public void setInterestRate(double rate) {
        interestManagementHttpClient.setInterestRate(rate);
    }

    public double getGlobalDailyRate() {
        return interestManagementHttpClient.getGlobalDailyRate();
    }

    public void setCalculationFrequency(String frequency) {
        interestManagementHttpClient.setCalculationFrequency(frequency);
    }

    public String getCalculationFrequency() {
        return interestManagementHttpClient.getCalculationFrequency();
    }

    public void applyInterest(boolean force) {
        interestManagementHttpClient.applyInterest(force);
    }

    public void skipTime(int periodsToSkip) {
        interestManagementHttpClient.skipTime(periodsToSkip);
    }
}
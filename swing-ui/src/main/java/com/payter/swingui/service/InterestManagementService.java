// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import com.payter.swingui.client.InterestManagementHttpClient;
import com.payter.swingui.model.InterestCalculationFrequency;
import com.payter.swingui.model.InterestRate;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementService {

    private final InterestManagementHttpClient interestManagementHttpClient;

    public InterestManagementService() {
        this.interestManagementHttpClient = new InterestManagementHttpClient();
    }

    public void setInterestRate(double rate) {
        interestManagementHttpClient.setInterestRate(rate);
    }

    public InterestRate getGlobalDailyRate() {
        return interestManagementHttpClient.getGlobalDailyRate();
    }

    public void setCalculationFrequency(String calculationFrequency) {
        interestManagementHttpClient.setCalculationFrequency(calculationFrequency);
    }

    public InterestCalculationFrequency getCalculationFrequency() {
        return interestManagementHttpClient.getCalculationFrequency();
    }

    public void applyInterest(boolean force) {
        interestManagementHttpClient.applyInterest(force);
    }

    public void skipTime(int periodsToSkip) {
        interestManagementHttpClient.skipTime(periodsToSkip);
    }
}
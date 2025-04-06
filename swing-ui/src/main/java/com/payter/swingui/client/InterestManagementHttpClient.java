// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import com.payter.common.util.ConfigUtil;
import com.payter.swingui.model.InterestFrequency;
import com.payter.swingui.model.InterestRate;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementHttpClient extends AbstractHttpClient {

    private static final String ENDPOINT = ConfigUtil.loadProperty("service.gateway.interestManagement.endpoint",
            "/interestmanagement");

    public InterestManagementHttpClient() {
        super();
    }

    public void setInterestRate(double rate) {
        try {
            sendPostRequest(ENDPOINT + "/rate", rate, Void.class);
        }
        catch(Exception e) {
            System.err.println("Failed to set interest rate: " + e.getMessage());
        }
    }

    public InterestRate getGlobalDailyRate() {
        try {
            Double rate = sendGetRequest(ENDPOINT + "/rate", Double.class);
            return new InterestRate(rate != null ? rate : 0.0);
        }
        catch(Exception e) {
            System.err.println("Failed to get interest rate: " + e.getMessage());
            return new InterestRate(0.0);
        }
    }

    public void setCalculationFrequency(String frequency) {
        try {
            sendPostRequest(ENDPOINT + "/frequency", frequency, Void.class);
        }
        catch(Exception e) {
            System.err.println("Failed to set calculation frequency: " + e.getMessage());
        }
    }

    public InterestFrequency getCalculationFrequency() {
        try {
            String frequency = sendGetRequest(ENDPOINT + "/frequency", String.class);
            return new InterestFrequency(frequency);
        }
        catch(Exception e) {
            System.err.println("Failed to get calculation frequency: " + e.getMessage());
            return new InterestFrequency();
        }
    }

    public void applyInterest(boolean force) {
        try {
            sendPostRequest(ENDPOINT + "/apply", force, Void.class);
        }
        catch(Exception e) {
            System.err.println("Failed to apply interest: " + e.getMessage());
        }
    }

    public void skipTime(int periodsToSkip) {
        try {
            sendPostRequest(ENDPOINT + "/skip-time", periodsToSkip, Void.class);
        }
        catch(Exception e) {
            System.err.println("Failed to skip time: " + e.getMessage());
        }
    }
}
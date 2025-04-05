// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import com.payter.common.util.ConfigUtil;

/**
 * 
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
            sendPostRequest(ENDPOINT + "/rate", String.valueOf(rate), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public double getGlobalDailyRate() {
        try {
            return sendGetRequest(ENDPOINT + "/rate", Double.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return 0.0d;
        }
    }

    public void setCalculationFrequency(String frequency) {
        try {
            sendPostRequest(ENDPOINT + "/frequency", frequency, Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public String getCalculationFrequency() {
        try {
            return sendGetRequest(ENDPOINT + "/frequency", String.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void applyInterest(boolean force) {
        try {
            sendPostRequest(ENDPOINT + "/apply", String.valueOf(force), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void skipTime(int periodsToSkip) {
        try {
            sendPostRequest(ENDPOINT + "/skip-time", String.valueOf(periodsToSkip), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
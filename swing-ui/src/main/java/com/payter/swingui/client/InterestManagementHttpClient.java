// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementHttpClient extends AbstractHttpClient {

    public InterestManagementHttpClient() {
        super();
    }

    @Override
    protected String getBaseUrl() {
        return "http://localhost:8002";
    }

    public void setInterestRate(double rate) {
        try {
            sendPostRequest("/interest/rate", String.valueOf(rate), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public double getGlobalDailyRate() {
        try {
            return sendGetRequest("/interest/rate", Double.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return 0.0d;
        }
    }

    public void setCalculationFrequency(String frequency) {
        try {
            sendPostRequest("/interest/frequency", frequency, Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public String getCalculationFrequency() {
        try {
            return sendGetRequest("/interest/frequency", String.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void applyInterest(boolean force) {
        try {
            sendPostRequest("/interest/apply", String.valueOf(force), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void skipTime(int periodsToSkip) {
        try {
            sendPostRequest("/interest/skip-time", String.valueOf(periodsToSkip), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
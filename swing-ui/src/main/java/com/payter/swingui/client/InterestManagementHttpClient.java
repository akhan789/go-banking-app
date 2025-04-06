// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.math.BigDecimal;

import com.payter.common.dto.interestmanagement.InterestManagementDTO;
import com.payter.common.dto.interestmanagement.InterestManagementRequestDTO;
import com.payter.common.util.ConfigUtil;
import com.payter.swingui.model.InterestCalculationFrequency;
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
            InterestManagementRequestDTO request = new InterestManagementRequestDTO(BigDecimal.valueOf(rate), null);
            sendPostRequest(ENDPOINT + "/rate", request, Void.class);
        }
        catch(Exception e) {
            System.err.println("Failed to set interest rate: " + e.getMessage());
        }
    }

    public InterestRate getGlobalDailyRate() {
        try {
            InterestManagementDTO response = sendGetRequest(ENDPOINT + "/rate", InterestManagementDTO.class);
            return response != null && response.getDailyRate() != null
                    ? new InterestRate(response.getDailyRate().doubleValue())
                    : new InterestRate(0.0);
        }
        catch(Exception e) {
            System.err.println("Failed to get interest rate: " + e.getMessage());
            return new InterestRate(0.0);
        }
    }

    public void setCalculationFrequency(String calculationFrequency) {
        try {
            InterestManagementRequestDTO request = new InterestManagementRequestDTO(null, calculationFrequency);
            sendPostRequest(ENDPOINT + "/calculationfrequency", request, Void.class);
        }
        catch(Exception e) {
            System.err.println("Failed to set calculation frequency: " + e.getMessage());
        }
    }

    public InterestCalculationFrequency getCalculationFrequency() {
        try {
            InterestManagementDTO response = sendGetRequest(ENDPOINT + "/calculationfrequency",
                    InterestManagementDTO.class);
            return response != null && response.getCalculationFrequency() != null
                    ? new InterestCalculationFrequency(response.getCalculationFrequency())
                    : new InterestCalculationFrequency("MONTHLY"); // Default to MONTHLY if null
        }
        catch(Exception e) {
            System.err.println("Failed to get calculation frequency: " + e.getMessage());
            return new InterestCalculationFrequency("MONTHLY");
        }
    }

    public void applyInterest(boolean force) {
        try {
            sendPostRequest(ENDPOINT + "/apply?force=" + force, null, Void.class);
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
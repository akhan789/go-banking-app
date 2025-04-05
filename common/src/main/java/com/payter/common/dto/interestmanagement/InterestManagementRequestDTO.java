// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.dto.interestmanagement;

import java.math.BigDecimal;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementRequestDTO {

    private BigDecimal dailyRate;
    private String calculationFrequency;

    public InterestManagementRequestDTO() {
    }

    public InterestManagementRequestDTO(BigDecimal dailyRate, String calculationFrequency) {
        this.dailyRate = dailyRate;
        this.calculationFrequency = calculationFrequency;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getCalculationFrequency() {
        return calculationFrequency;
    }

    public void setCalculationFrequency(String calculationFrequency) {
        this.calculationFrequency = calculationFrequency;
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.entity;

import java.math.BigDecimal;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagement {

    private Long id;
    private BigDecimal dailyRate;
    private CalculationFrequency calculationFrequency;

    public enum CalculationFrequency {
        DAILY, WEEKLY, MONTHLY
    }

    public InterestManagement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public CalculationFrequency getCalculationFrequency() {
        return calculationFrequency;
    }

    public void setCalculationFrequency(CalculationFrequency frequency) {
        this.calculationFrequency = frequency;
    }
}
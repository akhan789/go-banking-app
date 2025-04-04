// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagement {

    private long id;
    private BigDecimal dailyRate;
    private CalculationFrequency calculationFrequency;
    private LocalDateTime createdAt;

    public enum CalculationFrequency {
        DAILY, WEEKLY, MONTHLY
    }

    public InterestManagement() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
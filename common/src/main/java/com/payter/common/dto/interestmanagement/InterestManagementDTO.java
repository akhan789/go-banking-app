// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.dto.interestmanagement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementDTO {

    private long id;
    private BigDecimal dailyRate;
    private String calculationFrequency;
    private LocalDateTime createdAt;

    public InterestManagementDTO() {
    }

    public InterestManagementDTO(long id, BigDecimal dailyRate, String calculationFrequency, LocalDateTime createdAt) {
        this.id = id;
        this.dailyRate = dailyRate;
        this.calculationFrequency = calculationFrequency;
        this.createdAt = createdAt;
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

    public String getCalculationFrequency() {
        return calculationFrequency;
    }

    public void setCalculationFrequency(String calculationFrequency) {
        this.calculationFrequency = calculationFrequency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
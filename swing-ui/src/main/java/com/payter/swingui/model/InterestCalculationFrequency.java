// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.model;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestCalculationFrequency {

    private String calculationFrequency;

    public InterestCalculationFrequency() {
        this.calculationFrequency = "DAILY";
    }

    public InterestCalculationFrequency(String calcualationFrequency) {
        this.calculationFrequency = calcualationFrequency != null ? calcualationFrequency : "DAILY";
    }

    public String getCalculationFrequency() {
        return calculationFrequency;
    }

    public void setCalculationFrequency(String calcualationFrequency) {
        this.calculationFrequency = calcualationFrequency != null ? calcualationFrequency : "DAILY";
    }

    @Override
    public String toString() {
        return calculationFrequency != null ? calculationFrequency : "Unknown";
    }
}
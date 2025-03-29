// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.swingui.viewmodel;

/**
 * 
 * 
 * @author AK
 * @since 0.0.1_SNAPSHOT
 * @created 28 Mar 2025
 * @version $Revision$
 */
public class InterestManagementViewModel {

    private double interestRate;

    public void setInterestRate(double rate) {
        if(rate < 0) {
            throw new IllegalArgumentException("Rate must be a positive number.");
        }
        this.interestRate = rate;
        System.out.println("Interest rate set to: " + interestRate);
    }

    public void applyInterest() {
        // Example logic to apply interest, this is just a placeholder
        System.out.println("Applying interest at rate: " + interestRate);
    }
}
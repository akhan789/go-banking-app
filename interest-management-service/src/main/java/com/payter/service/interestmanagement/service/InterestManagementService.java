// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.service;

import java.math.BigDecimal;

import com.payter.service.interestmanagement.entity.InterestManagement;
import com.payter.service.interestmanagement.entity.InterestManagement.CalculationFrequency;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface InterestManagementService {

    void startInterestApplication();

    InterestManagement configureInterest(BigDecimal dailyRate, CalculationFrequency calculationFrequency)
            throws Exception;

    InterestManagement getLatestInterestManagement() throws Exception;
}
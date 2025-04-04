// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.service;

import java.math.BigDecimal;

import com.payter.service.balanceoperations.entity.BalanceOperation;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface BalanceOperationsService {

    BigDecimal getBalance(String accountId) throws Exception;

    BalanceOperation processCredit(BalanceOperation balanceOperation) throws Exception;

    BalanceOperation processDebit(BalanceOperation balanceOperation) throws Exception;
}
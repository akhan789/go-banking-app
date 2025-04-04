// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.repository;

import java.math.BigDecimal;
import java.util.List;

import com.payter.service.balanceoperations.entity.BalanceOperation;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface BalanceOperationsRepository {

    BalanceOperation save(BalanceOperation transaction) throws Exception;

    List<BalanceOperation> findByAccountId(String accountId) throws Exception;

    BigDecimal calculateBalance(String accountId) throws Exception;
}
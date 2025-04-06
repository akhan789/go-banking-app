// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.repository;

import java.sql.SQLException;
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

    BalanceOperation save(BalanceOperation balanceOperation) throws SQLException;

    List<BalanceOperation> findByAccountId(String accountId) throws SQLException;

    void saveTransfer(BalanceOperation debit, BalanceOperation credit) throws SQLException;
}
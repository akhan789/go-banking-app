// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.entity.Account.Status;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface AccountManagementRepository {

    Account save(Account account) throws Exception;

    Account findByAccountId(String accountId) throws Exception;

    List<Account> findAll() throws SQLException;

    void updateStatus(String accountId, Status status) throws Exception;

    void updateBalance(String accountId, BigDecimal balance) throws Exception;
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.service;

import com.payter.service.accountmanagement.entity.Account;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface AccountManagementService {

    Account createAccount(Account account) throws Exception;

    Account suspendAccount(String accountId) throws Exception;

    Account reactivateAccount(String accountId) throws Exception;

    void closeAccount(String accountId) throws Exception;

    Account getAccount(String accountId) throws Exception;
}
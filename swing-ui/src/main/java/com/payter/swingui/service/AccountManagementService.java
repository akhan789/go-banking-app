// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import java.math.BigDecimal;

import com.payter.common.dto.accountmanagement.CreateAccountRequestDTO;
import com.payter.swingui.client.AccountManagementHttpClient;
import com.payter.swingui.model.Account;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementService {

    private final AccountManagementHttpClient accountManagementHttpClient;

    public AccountManagementService() {
        this.accountManagementHttpClient = new AccountManagementHttpClient();
    }

    public Account createAccount(String accountName, double initialBalance, String currency) {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(accountName, BigDecimal.valueOf(initialBalance),
                currency);
        return accountManagementHttpClient.createAccount(request);
    }

    public Account getAccount(String accountId) {
        return accountManagementHttpClient.getAccount(accountId);
    }

    public Account suspendAccount(String accountId) {
        return accountManagementHttpClient.suspendAccount(accountId);
    }

    public Account reactivateAccount(String accountId) {
        return accountManagementHttpClient.reactivateAccount(accountId);
    }

    public Account closeAccount(String accountId) {
        return accountManagementHttpClient.closeAccount(accountId);
    }
}
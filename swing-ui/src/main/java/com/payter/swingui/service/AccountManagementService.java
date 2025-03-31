// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import com.payter.swingui.client.AccountManagementHttpClient;
import com.payter.swingui.model.Account;

/**
 * Account Service.
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementService {

    private AccountManagementHttpClient accountManagementHttpClient;

    public AccountManagementService() {
        this.accountManagementHttpClient = new AccountManagementHttpClient();
    }

    public Account createAccount(String accountName, double initialBalance, String currency) {
        Account account = new Account(null, accountName, initialBalance, "ACTIVE", currency);
        return accountManagementHttpClient.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return accountManagementHttpClient.getAccount(accountId);
    }

    public void suspendAccount(String accountId) {
        accountManagementHttpClient.suspendAccount(accountId);
    }

    public void reactivateAccount(String accountId) {
        accountManagementHttpClient.reactivateAccount(accountId);
    }

    public void closeAccount(String accountId) {
        accountManagementHttpClient.closeAccount(accountId);
    }
}
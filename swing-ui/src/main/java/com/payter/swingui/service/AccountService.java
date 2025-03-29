// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import com.payter.swingui.client.AccountHttpClient;
import com.payter.swingui.model.Account;

/**
 * Account Service.
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountService {

    private AccountHttpClient accountHttpClient;

    public AccountService() {
        this.accountHttpClient = new AccountHttpClient();
    }

    public Account createAccount(String accountName, double initialBalance, String currency) {
        Account account = new Account(null, accountName, initialBalance, "ACTIVE", currency);
        return accountHttpClient.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return accountHttpClient.getAccount(accountId);
    }

    public void suspendAccount(String accountId) {
        accountHttpClient.suspendAccount(accountId);
    }

    public void reactivateAccount(String accountId) {
        accountHttpClient.reactivateAccount(accountId);
    }

    public void closeAccount(String accountId) {
        accountHttpClient.closeAccount(accountId);
    }
}
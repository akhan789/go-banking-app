// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import com.payter.swingui.client.BalanceOperationsHttpClient;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsService {

    private BalanceOperationsHttpClient balanceOperationsHttpClient;

    public BalanceOperationsService() {
        this.balanceOperationsHttpClient = new BalanceOperationsHttpClient();
    }

    public void credit(String accountId, double amount) {
        balanceOperationsHttpClient.credit(accountId, amount);
    }

    public boolean debit(String accountId, double amount) {
        return balanceOperationsHttpClient.debit(accountId, amount);
    }

    public boolean transfer(String fromAccountId, String toAccountId, double amount) {
        return balanceOperationsHttpClient.transfer(fromAccountId, toAccountId, amount);
    }

    public double getBalance(String accountId) {
        return balanceOperationsHttpClient.getBalance(accountId);
    }

    public boolean isSufficientBalance(String accountId, double amount) {
        return balanceOperationsHttpClient.isSufficientBalance(accountId, amount);
    }
}
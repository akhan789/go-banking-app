// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.service;

import com.payter.swingui.client.BalanceOperationsHttpClient;
import com.payter.swingui.model.Balance;
import com.payter.swingui.model.BalanceOperation;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsService {

    private final BalanceOperationsHttpClient balanceOperationsHttpClient;

    public BalanceOperationsService() {
        this.balanceOperationsHttpClient = new BalanceOperationsHttpClient();
    }

    public BalanceOperation credit(String accountId, double amount) {
        return balanceOperationsHttpClient.credit(accountId, amount);
    }

    public BalanceOperation debit(String accountId, double amount) {
        return balanceOperationsHttpClient.debit(accountId, amount);
    }

    public BalanceOperation transfer(String fromAccountId, String toAccountId, double amount) {
        return balanceOperationsHttpClient.transfer(fromAccountId, toAccountId, amount);
    }

    public Balance getBalance(String accountId) {
        return balanceOperationsHttpClient.getBalance(accountId);
    }

    public boolean isSufficientBalance(String accountId, double amount) {
        return balanceOperationsHttpClient.isSufficientBalance(accountId, amount);
    }
}
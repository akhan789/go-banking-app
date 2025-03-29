// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.swingui.service;

import com.payter.swingui.model.Account;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class TransactionService {

    public void processCredit(Account account, double amount) {
        account.deposit(amount);
    }

    public boolean processDebit(Account account, double amount) {
        return account.debit(amount);
    }

    public boolean processTransfer(Account fromAccount, Account toAccount, double amount) {
        return fromAccount.transferTo(toAccount, amount);
    }
}
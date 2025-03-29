// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.model.Account;
import com.payter.swingui.service.AccountService;
import com.payter.swingui.service.TransactionService;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsViewModel {

    private AccountService accountService = new AccountService();
    private TransactionService transactionService = new TransactionService();

    public void processCreditTransaction(String accountId, double amount) {
        Account account = accountService.getAccount(accountId);
        if(account != null) {
            transactionService.processCredit(account, amount);
            System.out.println("Credited: " + amount);
        }
    }

    public void processDebitTransaction(String accountId, double amount) {
        Account account = accountService.getAccount(accountId);
        if(account != null) {
            if(transactionService.processDebit(account, amount)) {
                System.out.println("Debited: " + amount);
            }
            else {
                System.out.println("Insufficient funds.");
            }
        }
    }

    public void processTransferTransaction(String fromAccountId, String toAccountId, double amount) {
        Account fromAccount = accountService.getAccount(fromAccountId);
        Account toAccount = accountService.getAccount(toAccountId);
        if(fromAccount != null && toAccount != null) {
            if(transactionService.processTransfer(fromAccount, toAccount, amount)) {
                System.out.println("Transferred: " + amount);
            }
            else {
                System.out.println("Transfer failed.");
            }
        }
    }
}
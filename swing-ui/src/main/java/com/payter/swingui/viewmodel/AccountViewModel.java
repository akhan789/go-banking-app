// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.model.Account;
import com.payter.swingui.service.AccountService;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountViewModel {

    private AccountService accountService = new AccountService();

    public void createAccount(String accountName, String depositAmount, String currency)
            throws AccountViewModelException {
        if(accountName == null || accountName.isEmpty()) {
            throw new AccountViewModelException("Account Name can not be null or empty.");
        }
        if(depositAmount == null || depositAmount.isEmpty()) {
            throw new AccountViewModelException("Deposit amount can not be null or empty.");
        }
        double initialDeposit = 0.0d;
        try {
            initialDeposit = Double.parseDouble(depositAmount);
        }
        catch(NumberFormatException e) {
            throw new AccountViewModelException("Please enter a valid deposit amount.");
        }
        if(initialDeposit < 0) {
            throw new AccountViewModelException("Deposit amount must be positive.");
        }

        Account account = accountService.createAccount(accountName, initialDeposit, currency);
        System.out.println("Account created: " + account.getAccountId());
    }

    public void suspendAccount(String accountId) throws AccountViewModelException {
        if(accountId == null || accountId.isEmpty()) {
            throw new AccountViewModelException("Account Id can not be null or empty.");
        }
        accountService.suspendAccount(accountId);
        System.out.println("Account suspended: " + accountId);
    }

    public void reactivateAccount(String accountId) throws AccountViewModelException {
        if(accountId == null || accountId.isEmpty()) {
            throw new AccountViewModelException("Account Id can not be null or empty.");
        }
        accountService.reactivateAccount(accountId);
        System.out.println("Account reactivated: " + accountId);
    }

    public void closeAccount(String accountId) throws AccountViewModelException {
        if(accountId == null || accountId.isEmpty()) {
            throw new AccountViewModelException("Account Id can not be null or empty.");
        }
        accountService.closeAccount(accountId);
        System.out.println("Account closed: " + accountId);
    }
}
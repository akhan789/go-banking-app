// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.model.Account;
import com.payter.swingui.service.AccountManagementService;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementViewModel {

    private AccountManagementService accountService = new AccountManagementService();

    public void createAccount(String accountName, String depositAmount, String currency)
            throws AccountViewModelException {
        validateAccountName(accountName);
        double initialDeposit = validateDepositAmount(depositAmount);
        Account account = accountService.createAccount(accountName, initialDeposit, currency);

        if(account != null) {
            System.out.println("Account created: " + account.getAccountId());
        }
    }

    public void suspendAccount(String accountId) throws AccountViewModelException {
        validateAccountId(accountId);
        accountService.suspendAccount(accountId);
        System.out.println("Account suspended: " + accountId);
    }

    public void reactivateAccount(String accountId) throws AccountViewModelException {
        validateAccountId(accountId);
        accountService.reactivateAccount(accountId);
        System.out.println("Account reactivated: " + accountId);
    }

    public void closeAccount(String accountId) throws AccountViewModelException {
        validateAccountId(accountId);
        accountService.closeAccount(accountId);
        System.out.println("Account closed: " + accountId);
    }

    private void validateAccountName(String accountName) throws AccountViewModelException {
        if(accountName == null || accountName.isEmpty()) {
            throw new AccountViewModelException("Account Name can not be null or empty.");
        }
    }

    private void validateAccountId(String accountId) throws AccountViewModelException {
        if(accountId == null || accountId.isEmpty()) {
            throw new AccountViewModelException("Account Id can not be null or empty.");
        }
    }

    private double validateDepositAmount(String depositAmount) throws AccountViewModelException {
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
        return initialDeposit;
    }
}
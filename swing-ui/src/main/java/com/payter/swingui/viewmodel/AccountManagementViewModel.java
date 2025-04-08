// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.model.Account;
import com.payter.swingui.service.AccountManagementService;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementViewModel {

    private final AccountManagementService accountService;

    public AccountManagementViewModel() {
        this.accountService = new AccountManagementService();
    }

    public Account createAccount(String accountName, String depositAmount, String currency)
            throws AccountViewModelException {
        validateAccountName(accountName);
        double initialDeposit = validateDepositAmount(depositAmount);
        Account account = accountService.createAccount(accountName, initialDeposit, currency);
        if(account != null) {
            System.out.println("Account created: " + account.getAccountId());
            return account;
        }
        else {
            throw new AccountViewModelException("Failed to create account.");
        }
    }

    public Account suspendAccount(String accountId) throws AccountViewModelException {
        validateAccountId(accountId);
        Account updatedAccount = accountService.suspendAccount(accountId);
        if(updatedAccount != null) {
            System.out.println("Account suspended: " + accountId);
            return updatedAccount;
        }
        else {
            throw new AccountViewModelException("Failed to suspend account: " + accountId);
        }
    }

    public Account reactivateAccount(String accountId) throws AccountViewModelException {
        validateAccountId(accountId);
        Account updatedAccount = accountService.reactivateAccount(accountId);
        if(updatedAccount != null) {
            System.out.println("Account reactivated: " + accountId);
            return updatedAccount;
        }
        else {
            throw new AccountViewModelException("Failed to reactivate account: " + accountId);
        }
    }

    public Account closeAccount(String accountId) throws AccountViewModelException {
        validateAccountId(accountId);
        Account updatedAccount = accountService.closeAccount(accountId);
        if(updatedAccount != null) {
            System.out.println("Account closed: " + accountId);
            return updatedAccount;
        }
        else {
            throw new AccountViewModelException("Failed to close account: " + accountId);
        }
    }

    private void validateAccountName(String accountName) throws AccountViewModelException {
        if(accountName == null || accountName.trim().isEmpty()) {
            throw new AccountViewModelException("Account Name cannot be null or empty.");
        }
    }

    private void validateAccountId(String accountId) throws AccountViewModelException {
        if(accountId == null || accountId.trim().isEmpty()) {
            throw new AccountViewModelException("Account Id cannot be null or empty.");
        }
    }

    private double validateDepositAmount(String depositAmount) throws AccountViewModelException {
        if(depositAmount == null || depositAmount.trim().isEmpty()) {
            throw new AccountViewModelException("Deposit amount cannot be null or empty.");
        }
        double initialDeposit;
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
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.service.BalanceOperationsService;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsViewModel {

    private BalanceOperationsService balanceOperationsService = new BalanceOperationsService();

    public double getBalance(String accountId) {
        if(accountId == null || accountId.isEmpty()) {
            return 0.0d;
        }

        double balance = balanceOperationsService.getBalance(accountId);
        System.out.println("Account balance: " + balance);
        return balance;
    }

    public void credit(String accountId, String amountText) throws BalanceOperationsViewModelException {
        validateAccountId(accountId);
        double amount = validateAmount(amountText);
        balanceOperationsService.credit(accountId, amount);
        System.out.println("Credited: " + amount);
    }

    public void debit(String accountId, String amountText) throws BalanceOperationsViewModelException {
        validateAccountId(accountId);
        double amount = validateAmount(amountText);
        if(balanceOperationsService.debit(accountId, amount)) {
            System.out.println("Debited: " + amount);
        }
        else {
            throw new BalanceOperationsViewModelException("Insufficient funds.");
        }
    }

    public void transfer(String fromAccountId, String toAccountId, String amountText)
            throws BalanceOperationsViewModelException {
        validateFromAccountId(fromAccountId);
        validateToAccountId(toAccountId);
        double amount = validateAmount(amountText);
        if(balanceOperationsService.transfer(fromAccountId, toAccountId, amount)) {
            System.out.println("Transferred: " + amount);
        }
        else {
            throw new BalanceOperationsViewModelException("Transfer failed.");
        }
    }

    private void validateAccountId(String accountId) throws BalanceOperationsViewModelException {
        if(accountId == null || accountId.isEmpty()) {
            throw new BalanceOperationsViewModelException("Account Id can not be null or empty.");
        }
    }

    private void validateFromAccountId(String fromAccountId) throws BalanceOperationsViewModelException {
        if(fromAccountId == null || fromAccountId.isEmpty()) {
            throw new BalanceOperationsViewModelException("From Account Id can not be null or empty.");
        }
    }

    private void validateToAccountId(String toAccountId) throws BalanceOperationsViewModelException {
        if(toAccountId == null || toAccountId.isEmpty()) {
            throw new BalanceOperationsViewModelException("To Account Id can not be null or empty.");
        }
    }

    private double validateAmount(String amountText) throws BalanceOperationsViewModelException {
        double amount = 0.0d;
        try {
            amount = Double.parseDouble(amountText);
        }
        catch(NumberFormatException e) {
            throw new BalanceOperationsViewModelException("Please enter a valid amount.");
        }
        if(amount < 0) {
            throw new BalanceOperationsViewModelException("Amount must be positive.");
        }
        return amount;
    }
}
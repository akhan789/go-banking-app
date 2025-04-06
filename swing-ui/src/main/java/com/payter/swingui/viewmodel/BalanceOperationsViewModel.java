// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.viewmodel;

import com.payter.swingui.model.Balance;
import com.payter.swingui.model.BalanceOperation;
import com.payter.swingui.service.BalanceOperationsService;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsViewModel {

    private final BalanceOperationsService balanceOperationsService;

    public BalanceOperationsViewModel() {
        this.balanceOperationsService = new BalanceOperationsService();
    }

    public Balance getBalance(String accountId) throws BalanceOperationsViewModelException {
        validateAccountId(accountId);
        Balance balance = balanceOperationsService.getBalance(accountId);
        if(balance != null) {
            System.out.println("Account balance: " + balance.getBalance());
            return balance;
        }
        else {
            throw new BalanceOperationsViewModelException("Failed to retrieve balance for account: " + accountId);
        }
    }

    public BalanceOperation credit(String accountId, String amountText) throws BalanceOperationsViewModelException {
        validateAccountId(accountId);
        double amount = validateAmount(amountText);
        BalanceOperation operation = balanceOperationsService.credit(accountId, amount);
        if(operation != null) {
            System.out.println("Credited: " + amount);
            return operation;
        }
        else {
            throw new BalanceOperationsViewModelException("Failed to credit account: " + accountId);
        }
    }

    public BalanceOperation debit(String accountId, String amountText) throws BalanceOperationsViewModelException {
        validateAccountId(accountId);
        double amount = validateAmount(amountText);
        BalanceOperation operation = balanceOperationsService.debit(accountId, amount);
        if(operation != null) {
            System.out.println("Debited: " + amount);
            return operation;
        }
        else {
            throw new BalanceOperationsViewModelException(
                    "Failed to debit account: " + accountId + ". Possibly insufficient funds.");
        }
    }

    public BalanceOperation transfer(String fromAccountId, String toAccountId, String amountText)
            throws BalanceOperationsViewModelException {
        validateFromAccountId(fromAccountId);
        validateToAccountId(toAccountId);
        double amount = validateAmount(amountText);
        BalanceOperation operation = balanceOperationsService.transfer(fromAccountId, toAccountId, amount);
        if(operation != null) {
            System.out.println("Transferred: " + amount);
            return operation;
        }
        else {
            throw new BalanceOperationsViewModelException(
                    "Transfer failed from " + fromAccountId + " to " + toAccountId);
        }
    }

    private void validateAccountId(String accountId) throws BalanceOperationsViewModelException {
        if(accountId == null || accountId.trim().isEmpty()) {
            throw new BalanceOperationsViewModelException("Account Id cannot be null or empty.");
        }
    }

    private void validateFromAccountId(String fromAccountId) throws BalanceOperationsViewModelException {
        if(fromAccountId == null || fromAccountId.trim().isEmpty()) {
            throw new BalanceOperationsViewModelException("From Account Id cannot be null or empty.");
        }
    }

    private void validateToAccountId(String toAccountId) throws BalanceOperationsViewModelException {
        if(toAccountId == null || toAccountId.trim().isEmpty()) {
            throw new BalanceOperationsViewModelException("To Account Id cannot be null or empty.");
        }
    }

    private double validateAmount(String amountText) throws BalanceOperationsViewModelException {
        if(amountText == null || amountText.trim().isEmpty()) {
            throw new BalanceOperationsViewModelException("Amount cannot be null or empty.");
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        }
        catch(NumberFormatException e) {
            throw new BalanceOperationsViewModelException("Please enter a valid amount.");
        }
        if(amount <= 0) {
            throw new BalanceOperationsViewModelException("Amount must be positive.");
        }
        return amount;
    }
}
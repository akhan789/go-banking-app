// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Account model.
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class Account {

    private String accountId;
    private String accountName;
    private double balance;
    private String status;
    private String currency;
    private String creationTime;
    private List<String> statusHistory = new ArrayList<>();

    public Account() {
    }

    public Account(String accountId, String accountName, double balance, String status, String currency) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.balance = balance;
        this.status = status;
        this.currency = currency;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public List<String> getStatusHistory() {
        return statusHistory;
    }

    public void deposit(double amount) {
        if(!status.equals("SUSPENDED")) {
            balance += amount;
        }
    }

    public boolean debit(double amount) {
        if(!status.equals("SUSPENDED") && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public boolean transferTo(Account otherAccount, double amount) {
        if(!status.equals("SUSPENDED") && debit(amount)) {
            otherAccount.deposit(amount);
            return true;
        }
        return false;
    }

    public void suspend() {
        setStatus("SUSPENDED");
    }

    public void reactivate() {
        setStatus("ACTIVE");
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class Account {

    private long id;
    private String accountId;
    private String accountName;
    private BigDecimal balance;
    private String status; // ACTIVE, SUSPENDED, CLOSED
    private String currency; // GBP / EUR / JPY
    private LocalDateTime creationTime;
    private List<String> statusHistory;

    public Account() {
    }

    public Account(String accountId, String accountName, BigDecimal balance, String currency) {
        this.accountId = accountId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.currency = currency;
        this.status = "ACTIVE";
        this.creationTime = LocalDateTime.now();
        this.statusHistory = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public List<String> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<String> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
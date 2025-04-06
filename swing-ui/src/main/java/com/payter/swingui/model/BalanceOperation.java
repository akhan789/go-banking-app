// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperation {

    private long id;
    private String accountId;
    private String toAccountId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime timestamp;
    private long relatedBalanceOperationId;

    public BalanceOperation() {
    }

    public BalanceOperation(String accountId, BigDecimal amount, String type) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
    }

    public BalanceOperation(long id, String accountId, String toAccountId, BigDecimal amount, String type,
            LocalDateTime timestamp, long relatedBalanceOperationId) {
        this.id = id;
        this.accountId = accountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.relatedBalanceOperationId = relatedBalanceOperationId;
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

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long getRelatedBalanceOperationId() {
        return relatedBalanceOperationId;
    }

    public void setRelatedBalanceOperationId(long relatedBalanceOperationId) {
        this.relatedBalanceOperationId = relatedBalanceOperationId;
    }

    @Override
    public String toString() {
        String base = String.format("[%s] %s: %s - %.2f", timestamp != null ? timestamp.toString() : "N/A", type,
                accountId, amount != null ? amount : BigDecimal.ZERO);
        if(toAccountId != null && !toAccountId.isEmpty()) {
            base += " to " + toAccountId;
        }
        return base;
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.dto.accountmanagement;

import java.math.BigDecimal;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class CreateAccountRequestDTO {

    private String accountName;
    private BigDecimal balance;
    private String currency;

    public CreateAccountRequestDTO() {
    }

    public CreateAccountRequestDTO(String accountName, BigDecimal balance, String currency) {
        this.accountName = accountName;
        this.balance = balance;
        this.currency = currency;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
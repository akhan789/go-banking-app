// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.dto.balanceoperations;

import java.math.BigDecimal;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class CreditDebitRequestDTO {

    private String accountId;
    private BigDecimal amount;

    public CreditDebitRequestDTO() {
    }

    public CreditDebitRequestDTO(String accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
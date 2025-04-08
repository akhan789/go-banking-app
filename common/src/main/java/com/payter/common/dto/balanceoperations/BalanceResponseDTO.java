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
public class BalanceResponseDTO {

    private BigDecimal balance;

    public BalanceResponseDTO() {
    }

    public BalanceResponseDTO(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
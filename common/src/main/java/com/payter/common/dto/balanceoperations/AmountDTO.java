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
public class AmountDTO {

    private BigDecimal amount;

    public AmountDTO() {
    }

    public AmountDTO(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
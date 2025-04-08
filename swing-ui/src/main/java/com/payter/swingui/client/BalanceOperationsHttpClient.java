// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.math.BigDecimal;

import com.payter.common.dto.balanceoperations.BalanceOperationDTO;
import com.payter.common.dto.balanceoperations.BalanceResponseDTO;
import com.payter.common.dto.balanceoperations.CreditDebitRequestDTO;
import com.payter.common.dto.balanceoperations.TransferRequestDTO;
import com.payter.common.util.ConfigUtil;
import com.payter.swingui.model.Balance;
import com.payter.swingui.model.BalanceOperation;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsHttpClient extends AbstractHttpClient {

    private static final String ENDPOINT = ConfigUtil.loadProperty("service.gateway.balanceOperations.endpoint",
            "/balanceoperations");

    public BalanceOperationsHttpClient() {
        super();
    }

    public BalanceOperation credit(String accountId, double amount) {
        try {
            CreditDebitRequestDTO request = new CreditDebitRequestDTO(accountId, BigDecimal.valueOf(amount));
            BalanceOperationDTO dto = sendPostRequest(ENDPOINT + "/credit", request, BalanceOperationDTO.class);
            return mapToBalanceOperation(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to credit account: " + e.getMessage());
            return null;
        }
    }

    public BalanceOperation debit(String accountId, double amount) {
        try {
            CreditDebitRequestDTO request = new CreditDebitRequestDTO(accountId, BigDecimal.valueOf(amount));
            BalanceOperationDTO dto = sendPostRequest(ENDPOINT + "/debit", request, BalanceOperationDTO.class);
            return mapToBalanceOperation(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to debit account: " + e.getMessage());
            return null;
        }
    }

    public BalanceOperation transfer(String fromAccountId, String toAccountId, double amount) {
        try {
            TransferRequestDTO request = new TransferRequestDTO(fromAccountId, toAccountId, BigDecimal.valueOf(amount));
            BalanceOperationDTO dto = sendPostRequest(ENDPOINT + "/transfer", request, BalanceOperationDTO.class);
            return mapToBalanceOperation(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to transfer: " + e.getMessage());
            return null;
        }
    }

    public Balance getBalance(String accountId) {
        try {
            BalanceResponseDTO dto = sendGetRequest(ENDPOINT + "/balance/" + accountId, BalanceResponseDTO.class);
            return mapToBalance(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to get balance: " + e.getMessage());
            return new Balance(BigDecimal.ZERO);
        }
    }

    public boolean isSufficientBalance(String accountId, double amount) {
        try {
            Balance balance = getBalance(accountId);
            return balance.getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0;
        }
        catch(Exception e) {
            System.err.println("Failed to check sufficient balance: " + e.getMessage());
            return false;
        }
    }

    private BalanceOperation mapToBalanceOperation(BalanceOperationDTO dto) {
        if(dto == null)
            return null;
        //@formatter:off
        return new BalanceOperation(
            dto.getId(),
            dto.getAccountId(),
            dto.getToAccountId(),
            dto.getAmount(),
            dto.getType(),
            dto.getTimestamp(),
            dto.getRelatedBalanceOperationId()
        );
        //@formatter:on
    }

    private Balance mapToBalance(BalanceResponseDTO dto) {
        if(dto == null)
            return new Balance(BigDecimal.ZERO);
        return new Balance(dto.getBalance());
    }
}
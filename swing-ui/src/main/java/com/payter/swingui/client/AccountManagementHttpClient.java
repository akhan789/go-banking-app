// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import com.payter.common.dto.accountmanagement.AccountDTO;
import com.payter.common.dto.accountmanagement.CreateAccountRequestDTO;
import com.payter.common.util.ConfigUtil;
import com.payter.swingui.model.Account;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementHttpClient extends AbstractHttpClient {

    private static final String ENDPOINT = ConfigUtil.loadProperty("service.gateway.accountManagement.endpoint",
            "/accountmanagement");

    public AccountManagementHttpClient() {
        super();
    }

    public Account getAccount(String accountId) {
        try {
            AccountDTO dto = sendGetRequest(ENDPOINT + "/" + accountId, AccountDTO.class);
            return mapToAccount(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to get account: " + e.getMessage());
            return null;
        }
    }

    public Account createAccount(CreateAccountRequestDTO request) {
        try {
            AccountDTO dto = sendPostRequest(ENDPOINT, request, AccountDTO.class);
            return mapToAccount(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to create account: " + e.getMessage());
            return null;
        }
    }

    public Account suspendAccount(String accountId) {
        try {
            AccountDTO dto = sendPutRequest(ENDPOINT + "/" + accountId + "/suspend", null, AccountDTO.class);
            return mapToAccount(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to suspend account: " + e.getMessage());
            return null;
        }
    }

    public Account reactivateAccount(String accountId) {
        try {
            AccountDTO dto = sendPutRequest(ENDPOINT + "/" + accountId + "/reactivate", null, AccountDTO.class);
            return mapToAccount(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to reactivate account: " + e.getMessage());
            return null;
        }
    }

    public Account closeAccount(String accountId) {
        try {
            AccountDTO dto = sendPutRequest(ENDPOINT + "/" + accountId + "/close", null, AccountDTO.class);
            return mapToAccount(dto);
        }
        catch(Exception e) {
            System.err.println("Failed to close account: " + e.getMessage());
            return null;
        }
    }

    private Account mapToAccount(AccountDTO dto) {
        if(dto == null) {
            return null;
        }
        //@formatter:off
        return new Account(
            dto.getAccountId(),
            dto.getAccountName(),
            dto.getBalance(),
            dto.getStatus(),
            dto.getCurrency(),
            dto.getCreationTime(),
            dto.getStatusHistory()
        );
        //@formatter:on
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import com.payter.common.util.ConfigUtil;

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

    public void credit(String accountId, double amount) {
        try {
            sendPostRequest(ENDPOINT + "/" + accountId + "/credit", String.valueOf(amount), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean debit(String accountId, double amount) {
        try {
            return sendPostRequest(ENDPOINT + "/" + accountId + "/debit", String.valueOf(amount), Boolean.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean transfer(String fromAccountId, String toAccountId, double amount) {
        try {
            // Directly passing parameters for the transfer request
            String transferUrl = String.format(ENDPOINT + "/%s/transfer/%s", fromAccountId, toAccountId);
            return sendPostRequest(transferUrl, String.valueOf(amount), Boolean.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public double getBalance(String accountId) {
        try {
            return sendGetRequest(ENDPOINT + "/" + accountId, Double.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return 0.0d;
        }
    }

    public boolean isSufficientBalance(String accountId, double amount) {
        try {
            double balance = getBalance(accountId);
            return balance >= amount;
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
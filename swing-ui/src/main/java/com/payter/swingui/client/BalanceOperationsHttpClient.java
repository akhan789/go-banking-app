// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsHttpClient extends AbstractHttpClient {

    public BalanceOperationsHttpClient() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBaseUrl() {
        return "http://localhost:8001";
    }

    // Credit an account (POST request for credit)
    public void credit(String accountId, double amount) {
        try {
            sendPostRequest("/balance/" + accountId + "/credit", String.valueOf(amount), Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Debit an account (POST request for debit)
    public boolean debit(String accountId, double amount) {
        try {
            return sendPostRequest("/balance/" + accountId + "/debit", String.valueOf(amount), Boolean.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    // Transfer between two accounts (POST request for transfer)
    public boolean transfer(String fromAccountId, String toAccountId, double amount) {
        try {
            // Directly passing parameters for the transfer request
            String transferUrl = String.format("/balance/%s/transfer/%s", fromAccountId, toAccountId);
            return sendPostRequest(transferUrl, String.valueOf(amount), Boolean.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    // Get the balance of an account (GET request)
    public double getBalance(String accountId) {
        try {
            return sendGetRequest("/balance/" + accountId, Double.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return 0.0d;
        }
    }

    // Check if an account has sufficient balance (GET request)
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
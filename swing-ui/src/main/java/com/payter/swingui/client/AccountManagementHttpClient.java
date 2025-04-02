// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import com.payter.swingui.model.Account;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementHttpClient extends AbstractHttpClient {

    public AccountManagementHttpClient() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBaseUrl() {
        return "http://localhost:8000";
    }

    // Create an account (POST request)
    public Account createAccount(Account account) {
        try {
            return sendPostRequest("/accounts", convertToJSONString(account), Account.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    // Get account details (GET request)
    public Account getAccount(String accountId) {
        try {
            return sendGetRequest("/accounts/" + accountId, Account.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    // Suspend an account (POST request for suspension)
    public void suspendAccount(String accountId) {
        try {
            sendPostRequest("/accounts/" + accountId + "/suspend", null, Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Reactivate an account (POST request for reactivation)
    public void reactivateAccount(String accountId) {
        try {
            sendPostRequest("/accounts/" + accountId + "/reactivate", null, Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Close account (POST request for close)
    public void closeAccount(String accountId) {
        try {
            sendPostRequest("/accounts/" + accountId + "/close", null, Void.class);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
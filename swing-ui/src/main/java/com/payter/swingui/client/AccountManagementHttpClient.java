// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import com.payter.common.util.ConfigUtil;
import com.payter.swingui.model.Account;

/**
 * 
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
            return sendGetRequest(ENDPOINT + "/" + accountId, Account.class);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Account createAccount(Account account) {
        try {
            return sendPostRequest(ENDPOINT, convertToJSONString(account), Account.class);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void suspendAccount(String accountId) {
        try {
            sendPutRequest(ENDPOINT + "/" + accountId + "/suspend", null, Void.class);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public void reactivateAccount(String accountId) {
        try {
            sendPutRequest(ENDPOINT + "/" + accountId + "/reactivate", null, Void.class);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public void closeAccount(String accountId) {
        try {
            sendPutRequest(ENDPOINT + "/" + accountId + "/close", null, Void.class);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
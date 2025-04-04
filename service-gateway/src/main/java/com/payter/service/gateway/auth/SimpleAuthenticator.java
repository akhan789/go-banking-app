// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class SimpleAuthenticator implements Authenticator {

    private final Map<String, String> apiKeys;

    public SimpleAuthenticator() {
        this.apiKeys = new HashMap<>();
        apiKeys.put("user1", "api-key-12345");
        apiKeys.put("user2", "api-key-67890");
    }

    @Override
    public boolean isValidApiKey(String apiKey) {
        return apiKey != null && apiKeys.containsValue(apiKey);
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.auth;

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
        // user and key.
        apiKeys.put("internal", "internal");
        apiKeys.put("default", "default_api_key");
    }

    @Override
    public boolean isValidApiKey(String apiKey) {
        // TODO: authenticate user too ?
        return apiKey != null && apiKeys.containsValue(apiKey);
    }
}
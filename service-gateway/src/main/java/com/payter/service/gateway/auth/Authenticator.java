// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway.auth;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface Authenticator {

    boolean isValidApiKey(String apiKey);
}
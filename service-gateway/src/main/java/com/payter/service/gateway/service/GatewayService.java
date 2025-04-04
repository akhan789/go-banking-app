// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway.service;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface GatewayService {

    String forwardRequest(String path, String method, String body, String apiKey) throws Exception;
}
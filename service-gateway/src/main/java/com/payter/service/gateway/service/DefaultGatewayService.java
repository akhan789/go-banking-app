// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway.service;

import java.util.HashMap;
import java.util.Map;

import com.payter.common.auth.Authenticator;
import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.ConfigUtil;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class DefaultGatewayService implements GatewayService {

    private final Authenticator authenticator;
    private final HttpClientService httpClientService;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public DefaultGatewayService(Authenticator authenticator, HttpClientService httpClientService) {
        this.authenticator = authenticator;
        this.httpClientService = httpClientService;
    }

    @Override
    public String forwardRequest(String path, String method, String body, String apiKey) throws Exception {
        if(!authenticator.isValidApiKey(apiKey)) {
            logUnauthorizedAttempt(path, method, apiKey);
            ErrorResponseDTO error = new ErrorResponseDTO("Unauthorized - Invalid or missing API key");
            return parser.serialise(error);
        }
        String targetUrl;
        if(path.startsWith(ConfigUtil.loadProperty("gateway.accountManagement.endpoint", "/accountmanagement"))) {
            targetUrl = ConfigUtil.loadProperty("gateway.accountManagement.url", "http://localhost:8001") + path;
        }
        else if(path.startsWith(ConfigUtil.loadProperty("gateway.balanceOperations.endpoint", "/balanceoperations"))) {
            targetUrl = ConfigUtil.loadProperty("gateway.balanceOperations.url", "http://localhost:8002") + path;
        }
        else if(path
                .startsWith(ConfigUtil.loadProperty("gateway.interestManagement.endpoint", "/interestmanagement"))) {
            targetUrl = ConfigUtil.loadProperty("gateway.interestManagement.url", "http://localhost:8003") + path;
        }
        else if(path.startsWith(ConfigUtil.loadProperty("gateway.auditLogging.endpoint", "/auditlogging"))) {
            targetUrl = ConfigUtil.loadProperty("gateway.auditLogging.url", "http://localhost:8004") + path;
        }
        else {
            ErrorResponseDTO error = new ErrorResponseDTO("Invalid path");
            return parser.serialise(error);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", apiKey);
        switch(method) {
            case "GET":
                return httpClientService.get(headers, targetUrl);
            case "POST":
                return httpClientService.post(headers, targetUrl, body != null ? body : "");
            case "PUT":
                return httpClientService.put(headers, targetUrl, body);
            default:
                ErrorResponseDTO error = new ErrorResponseDTO("Method not supported");
                return parser.serialise(error);
        }
    }

    private void logUnauthorizedAttempt(String path, String method, String apiKey) {
        String message = "Unauthorized attempt: " + method + " " + path + " with API key: "
                + (apiKey != null ? apiKey : "none");
        httpClientService.postAsync(null, ConfigUtil.loadProperty("gateway.auditLogging.url", "http://localhost:8004")
                + ConfigUtil.loadProperty("gateway.auditLogging.endpoint", "/auditlogging"), message);
    }
}
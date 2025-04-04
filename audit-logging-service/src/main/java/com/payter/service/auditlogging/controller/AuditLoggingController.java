// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.payter.common.http.HttpClientService;
import com.payter.service.auditlogging.service.AuditLoggingService;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingController {

    // TODO: configurable.
    private static final String VALID_API_KEY = "default_api_key";

    private final AuditLoggingService service;

    public AuditLoggingController(AuditLoggingService service) {
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            if(!isValidApiKey(exchange)) {
                HttpClientService.sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
                return;
            }

            String method = exchange.getRequestMethod();

            switch(method) {
                case "POST":
                    handlePost(exchange);
                    break;
                default:
                    HttpClientService.sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
            }
        }
        catch(Exception e) {
            HttpClientService.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private boolean isValidApiKey(HttpExchange exchange) {
        String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
        return apiKey != null && apiKey.equals(VALID_API_KEY);
    }

    private void handlePost(HttpExchange exchange) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            String message = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            service.log(message);
            HttpClientService.sendResponse(exchange, 204, "");
        }
    }
}
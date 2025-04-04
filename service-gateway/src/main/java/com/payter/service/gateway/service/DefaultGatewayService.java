// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway.service;

import java.net.URI;
import java.net.http.HttpRequest;

import com.payter.common.http.HttpClientService;
import com.payter.service.gateway.auth.Authenticator;

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

    public DefaultGatewayService(Authenticator authenticator, HttpClientService httpClientService) {
        this.authenticator = authenticator;
        this.httpClientService = httpClientService;
    }

    @Override
    public String forwardRequest(String path, String method, String body, String apiKey) throws Exception {
        if(!authenticator.isValidApiKey(apiKey)) {
            logUnauthorizedAttempt(path, method, apiKey);
            return "{\"error\": \"Unauthorized - Invalid or missing API key\"}";
        }

        String targetUrl;
        if(path.startsWith("/accountmanagement")) {
            targetUrl = "http://localhost:8001" + path;
        }
        else if(path.startsWith("/balanceoperations")) {
            targetUrl = "http://localhost:8002" + path;
        }
        else if(path.startsWith("/interestmanagement")) {
            targetUrl = "http://localhost:8003" + path;
        }
        else if(path.startsWith("/auditlogging")) {
            targetUrl = "http://localhost:8004" + path;
        }
        else {
            return "{\"error\": \"Invalid path\"}";
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(targetUrl)).header("X-API-Key",
                apiKey);

        switch(method) {
            case "GET":
                requestBuilder.GET();
                return httpClientService.get(targetUrl);
            case "POST":
                return httpClientService.post(targetUrl, body != null ? body : "");
            case "PUT":
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
                return httpClientService.post(targetUrl, body);
            case "DELETE":
                requestBuilder.DELETE();
                return httpClientService.get(targetUrl);
            default:
                return "{\"error\": \"Method not supported\"}";
        }
    }

    private void logUnauthorizedAttempt(String path, String method, String apiKey) {
        httpClientService.postAsync("http://localhost:8004/auditlogging", "Unauthorized attempt: " + method + " " + path
                + " with API key: " + (apiKey != null ? apiKey : "none"));
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway.controller;

import java.io.IOException;

import com.payter.service.gateway.service.GatewayService;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class GatewayController {

    private final GatewayService service;

    public GatewayController(GatewayService service) {
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String body = method.equals("POST") || method.equals("PUT")
                    ? new String(exchange.getRequestBody().readAllBytes())
                    : null;
            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
            String response = service.forwardRequest(path, method, body, apiKey);
            sendResponse(exchange, getStatusCode(method, response), response);
        }
        catch(Exception e) {
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private int getStatusCode(String method, String response) {
        if(response.contains("error")) {
            if(response.contains("Unauthorized"))
                return 401;
            return 400;
        }
        switch(method) {
            case "POST":
                return 201;
            case "GET":
            case "PUT":
                return 200;
            case "DELETE":
                return 204;
            default:
                return 405;
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.length());
        try(java.io.OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
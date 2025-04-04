// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.controller;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.service.balanceoperations.entity.BalanceOperation;
import com.payter.service.balanceoperations.service.BalanceOperationsService;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class BalanceOperationsController {

    // TODO: configurable.
    private static final String VALID_API_KEY = "default_api_key";

    private final BalanceOperationsService service;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public BalanceOperationsController(BalanceOperationsService service) {
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            if(!isValidApiKey(exchange)) {
                HttpClientService.sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String[] pathSegments = path.split("/");

            switch(method) {
                case "GET":
                    handleGet(exchange, path, pathSegments);
                    break;
                case "POST":
                    handlePost(exchange, path);
                    break;
                default:
                    HttpClientService.sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            HttpClientService.sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
        }
    }

    private void handleGet(HttpExchange exchange, String path, String[] pathSegments) throws Exception {
        if(path.startsWith("/transactions/balance/")) {
            if(pathSegments.length < 4 || pathSegments[3].isEmpty()) {
                HttpClientService.sendResponse(exchange, 400, "{\"error\": \"Invalid account ID\"}");
                return;
            }
            String accountId = pathSegments[3];
            BigDecimal balance = service.getBalance(accountId);
            HttpClientService.sendResponse(exchange, 200, parser.serialise(balance));
        }
    }

    private void handlePost(HttpExchange exchange, String path) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            BalanceOperation balanceOperation = parser.deserialise(body, BalanceOperation.class);
            if(balanceOperation.getAccountId() == null || balanceOperation.getAmount() == null) {
                HttpClientService.sendResponse(exchange, 400, "{\"error\": \"Missing required fields\"}");
                return;
            }
            if(path.endsWith("/credit")) {
                BalanceOperation created = service.processCredit(balanceOperation);
                HttpClientService.sendResponse(exchange, 201, parser.serialise(created));
            }
            else if(path.endsWith("/debit")) {
                BalanceOperation created = service.processDebit(balanceOperation);
                HttpClientService.sendResponse(exchange, 201, parser.serialise(created));
            }
            else {
                HttpClientService.sendResponse(exchange, 400, "{\"error\": \"Invalid transaction type\"}");
            }
        }
    }

    private boolean isValidApiKey(HttpExchange exchange) {
        String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
        return apiKey != null && apiKey.equals(VALID_API_KEY);
    }
}
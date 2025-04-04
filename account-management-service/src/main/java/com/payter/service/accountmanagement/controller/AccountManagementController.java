// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.service.AccountManagementService;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountManagementController {

    // TODO: configurable.
    private static final String VALID_API_KEY = "default_api_key";

    private final AccountManagementService service;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public AccountManagementController(AccountManagementService service) {
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

            if(pathSegments.length < 3) {
                HttpClientService.sendResponse(exchange, 400, "{\"error\": \"Invalid request format\"}");
                return;
            }

            switch(method) {
                case "GET":
                    handleGet(exchange, pathSegments);
                    break;
                case "PUT":
                    handlePut(exchange, path, pathSegments);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, pathSegments);
                    break;
                default:
                    HttpClientService.sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
            }
        }
        catch(NumberFormatException e) {
            HttpClientService.sendResponse(exchange, 400, "{\"error\": \"Invalid account ID format\"}");
        }
        catch(IOException e) {
            throw e;
        }
        catch(Exception e) {
            e.printStackTrace();
            HttpClientService.sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
        }
    }

    private boolean isValidApiKey(HttpExchange exchange) {
        String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
        return apiKey != null && apiKey.equals(VALID_API_KEY);
    }

    private void handleGet(HttpExchange exchange, String[] pathSegments) throws Exception {
        String accountId = parseAccountId(pathSegments);
        HttpClientService.sendResponse(exchange, 200, parser.serialise(service.getAccount(accountId)));
    }

    private void handlePut(HttpExchange exchange, String path, String[] pathSegments) throws Exception {
        String accountId = parseAccountId(pathSegments);

        if(path.endsWith("/suspend")) {
            HttpClientService.sendResponse(exchange, 200, parser.serialise(service.suspendAccount(accountId)));
        }
        else if(path.endsWith("/reactivate")) {
            HttpClientService.sendResponse(exchange, 200, parser.serialise(service.reactivateAccount(accountId)));
        }
        else if(path.endsWith("/close")) {
            service.closeAccount(accountId);
            HttpClientService.sendResponse(exchange, 204, "");
        }
        else {
            HttpClientService.sendResponse(exchange, 400, "{\"error\": \"Invalid request\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Account account = parser.deserialise(body, Account.class);
            if(account.getAccountId() == null || account.getStatus() == null) {
                HttpClientService.sendResponse(exchange, 400, "{\"error\": \"Missing required fields\"}");
                return;
            }
            Account created = service.createAccount(account);
            HttpClientService.sendResponse(exchange, 201, parser.serialise(created));
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathSegments) throws Exception {
        String accountId = parseAccountId(pathSegments);
        service.closeAccount(accountId);
        HttpClientService.sendResponse(exchange, 204, "");
    }

    private String parseAccountId(String[] pathSegments) throws NumberFormatException {
        if(pathSegments.length < 3 || pathSegments[2].isEmpty()) {
            throw new NumberFormatException("Invalid account ID format");
        }
        return pathSegments[2];
    }
}
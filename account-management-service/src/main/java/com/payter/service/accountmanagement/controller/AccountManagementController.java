// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.payter.common.http.HttpUtil;
import com.payter.common.parser.Parser;
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

    private static final String VALID_API_KEY = "api-key-12345";

    private final AccountManagementService service;
    private final Parser parser;

    public AccountManagementController(AccountManagementService service, Parser parser) {
        this.service = service;
        this.parser = parser;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            if(!isValidApiKey(exchange)) {
                HttpUtil.sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String[] pathSegments = path.split("/");

            if(pathSegments.length < 3) {
                HttpUtil.sendResponse(exchange, 400, "{\"error\": \"Invalid request format\"}");
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
                    HttpUtil.sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
            }
        }
        catch(NumberFormatException e) {
            HttpUtil.sendResponse(exchange, 400, "{\"error\": \"Invalid account ID format\"}");
        }
        catch(IOException e) {
            throw e;
        }
        catch(Exception e) {
            HttpUtil.sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
        }
    }

    private boolean isValidApiKey(HttpExchange exchange) {
        String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
        return VALID_API_KEY.equals(apiKey);
    }

    private void handleGet(HttpExchange exchange, String[] pathSegments) throws Exception {
        long accountId = parseAccountId(pathSegments);
        HttpUtil.sendResponse(exchange, 200, parser.serialise(service.getAccount(accountId)));
    }

    private void handlePut(HttpExchange exchange, String path, String[] pathSegments) throws Exception {
        long accountId = parseAccountId(pathSegments);
        if(path.endsWith("/suspend")) {
            HttpUtil.sendResponse(exchange, 200, parser.serialise(service.suspendAccount(accountId)));
        }
        else if(path.endsWith("/reactivate")) {
            HttpUtil.sendResponse(exchange, 200, parser.serialise(service.reactivateAccount(accountId)));
        }
        else {
            HttpUtil.sendResponse(exchange, 400, "{\"error\": \"Invalid request\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Account account = parser.deserialise(body, Account.class);
            Account created = service.createAccount(account);
            HttpUtil.sendResponse(exchange, 201, parser.serialise(created));
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathSegments) throws Exception {
        long accountId = parseAccountId(pathSegments);
        service.closeAccount(accountId);
        HttpUtil.sendResponse(exchange, 204, "");
    }

    private long parseAccountId(String[] pathSegments) throws NumberFormatException {
        return Long.parseLong(pathSegments[2]);
    }
}
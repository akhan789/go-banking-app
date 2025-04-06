// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.controller;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.payter.common.auth.Authenticator;
import com.payter.common.dto.accountmanagement.AccountDTO;
import com.payter.common.dto.accountmanagement.CreateAccountRequestDTO;
import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.ConfigUtil;
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

    private final Authenticator authenticator;
    private final AccountManagementService service;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public AccountManagementController(Authenticator authenticator, AccountManagementService service) {
        this.authenticator = authenticator;
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String[] pathSegments = path.split("/");
            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
            if(!authenticator.isValidApiKey(apiKey)) {
                ErrorResponseDTO error = new ErrorResponseDTO("Unauthorized - Invalid or missing API key");
                HttpClientService.sendResponse(exchange, 401, parser.serialise(error));
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
                    ErrorResponseDTO error = new ErrorResponseDTO("Method Not Allowed");
                    HttpClientService.sendResponse(exchange, 405, parser.serialise(error));
            }
        }
        catch(NumberFormatException e) {
            ErrorResponseDTO error = new ErrorResponseDTO("Invalid account ID format");
            try {
                HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
            }
            catch(Exception e1) {
                throw new IOException(e1);
            }
        }
        catch(IOException e) {
            throw e;
        }
        catch(Exception e) {
            e.printStackTrace();
            ErrorResponseDTO error = new ErrorResponseDTO("Internal Server Error: " + e.getMessage());
            try {
                HttpClientService.sendResponse(exchange, 500, parser.serialise(error));
            }
            catch(Exception e1) {
                throw new IOException(e1);
            }
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathSegments) throws Exception {
        if(pathSegments != null && pathSegments.length > 2) {
            String accountId = parseAccountId(pathSegments);
            Account account = service.getAccount(accountId);
            AccountDTO response = new AccountDTO(account.getId(), account.getAccountId(), account.getAccountName(),
                    account.getBalance(), account.getStatus().name(), account.getCurrency().name(),
                    account.getCreationTime(), account.getStatusHistory());
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
        else {
            List<Account> accounts = service.getAllAccounts();
            //@formatter:off
            List<AccountDTO> responseList = accounts.stream()
                .map(account -> new AccountDTO(
                    account.getId(),
                    account.getAccountId(),
                    account.getAccountName(),
                    account.getBalance(),
                    account.getStatus().name(),
                    account.getCurrency().name(),
                    account.getCreationTime(),
                    account.getStatusHistory()))
                .toList();
            //@formatter:on
            HttpClientService.sendResponse(exchange, 200, parser.serialise(responseList));
        }
    }

    private void handlePut(HttpExchange exchange, String path, String[] pathSegments) throws Exception {
        String accountId = parseAccountId(pathSegments);

        if(path.endsWith(ConfigUtil.loadProperty("accountManagement.suspend.endpoint", "/suspend"))) {
            Account updated = service.suspendAccount(accountId);
            AccountDTO response = new AccountDTO(updated.getId(), updated.getAccountId(), updated.getAccountName(),
                    updated.getBalance(), updated.getStatus().name(), updated.getCurrency().name(),
                    updated.getCreationTime(), updated.getStatusHistory());
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
        else if(path.endsWith(ConfigUtil.loadProperty("accountManagement.reactivate.endpoint", "/reactivate"))) {
            Account updated = service.reactivateAccount(accountId);
            AccountDTO response = new AccountDTO(updated.getId(), updated.getAccountId(), updated.getAccountName(),
                    updated.getBalance(), updated.getStatus().name(), updated.getCurrency().name(),
                    updated.getCreationTime(), updated.getStatusHistory());
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
        else if(path.endsWith(ConfigUtil.loadProperty("accountManagement.close.endpoint", "/close"))) {
            Account updated = service.closeAccount(accountId);
            AccountDTO response = new AccountDTO(updated.getId(), updated.getAccountId(), updated.getAccountName(),
                    updated.getBalance(), updated.getStatus().name(), updated.getCurrency().name(),
                    updated.getCreationTime(), updated.getStatusHistory());
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
        else if(path.endsWith("/credit") || path.endsWith("/debit")) {
            try(InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> request = parser.deserialiseMap(body, String.class, String.class);
                BigDecimal amount = new BigDecimal(request.get("amount"));
                Account updated = path.endsWith("/credit") ? service.creditAccount(accountId, amount)
                        : service.debitAccount(accountId, amount);
                AccountDTO response = new AccountDTO(updated.getId(), updated.getAccountId(), updated.getAccountName(),
                        updated.getBalance(), updated.getStatus().name(), updated.getCurrency().name(),
                        updated.getCreationTime(), updated.getStatusHistory());
                HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
            }
        }
        else {
            ErrorResponseDTO error = new ErrorResponseDTO("Invalid request");
            HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
        }
    }

    private void handlePost(HttpExchange exchange) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            CreateAccountRequestDTO request = parser.deserialise(body, CreateAccountRequestDTO.class);
            if(request.getAccountName() == null || request.getBalance() == null) {
                ErrorResponseDTO error = new ErrorResponseDTO("Missing required fields: accountName or balance");
                HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                return;
            }
            Account account = new Account(UUID.randomUUID().toString().split("-")[0], // Generate unique ID
                    request.getAccountName(), request.getBalance(),
                    request.getCurrency() != null ? Account.Currency.valueOf(request.getCurrency())
                            : Account.Currency.GBP);
            Account created = service.createAccount(account);
            AccountDTO response = new AccountDTO(created.getId(), created.getAccountId(), created.getAccountName(),
                    created.getBalance(), created.getStatus().name(), created.getCurrency().name(),
                    created.getCreationTime(), created.getStatusHistory());
            HttpClientService.sendResponse(exchange, 201, parser.serialise(response));
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathSegments) throws Exception {
        String accountId = parseAccountId(pathSegments);
        Account updated = service.closeAccount(accountId);
        AccountDTO response = new AccountDTO(updated.getId(), updated.getAccountId(), updated.getAccountName(),
                updated.getBalance(), updated.getStatus().name(), updated.getCurrency().name(),
                updated.getCreationTime(), updated.getStatusHistory());
        HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
    }

    private String parseAccountId(String[] pathSegments) throws NumberFormatException {
        if(pathSegments.length < 3 || pathSegments[2].isEmpty()) {
            throw new NumberFormatException("Invalid account ID format");
        }
        return pathSegments[2];
    }
}
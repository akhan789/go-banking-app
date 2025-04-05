// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.controller;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import com.payter.common.auth.Authenticator;
import com.payter.common.dto.balanceoperations.BalanceOperationDTO;
import com.payter.common.dto.balanceoperations.BalanceResponseDTO;
import com.payter.common.dto.balanceoperations.CreditDebitRequestDTO;
import com.payter.common.dto.balanceoperations.TransferRequestDTO;
import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.ConfigUtil;
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

    private final Authenticator authenticator;
    private final BalanceOperationsService service;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public BalanceOperationsController(Authenticator authenticator, BalanceOperationsService service) {
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
                    handleGet(exchange, path, pathSegments);
                    break;
                case "POST":
                    handlePost(exchange, path, pathSegments);
                    break;
                default:
                    ErrorResponseDTO error = new ErrorResponseDTO("Method Not Allowed");
                    HttpClientService.sendResponse(exchange, 405, parser.serialise(error));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            ErrorResponseDTO error = new ErrorResponseDTO("Internal Server Error");
            try {
                HttpClientService.sendResponse(exchange, 500, parser.serialise(error));
            }
            catch(Exception e1) {
                throw new IOException(e1);
            }
        }
    }

    private void handleGet(HttpExchange exchange, String path, String[] pathSegments) throws Exception {
        if(path.startsWith(ConfigUtil.loadProperty("balanceoperations.endpoint", "/balanceoperations")
                + ConfigUtil.loadProperty("balanceoperations.balance.endpoint", "/balance"))) {
            if(pathSegments.length < 4 || pathSegments[3].isEmpty()) {
                ErrorResponseDTO error = new ErrorResponseDTO("Invalid account ID");
                HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                return;
            }
            String accountId = pathSegments[3];
            BigDecimal balance = service.getBalance(accountId);
            BalanceResponseDTO response = new BalanceResponseDTO(balance);
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
    }

    private void handlePost(HttpExchange exchange, String path, String[] pathSegments) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if(path.endsWith(ConfigUtil.loadProperty("balanceoperations.credit.endpoint", "/credit"))) {
                postCredit(exchange, body);
            }
            else if(path.endsWith(ConfigUtil.loadProperty("balanceoperations.debit.endpoint", "/debit"))) {
                postDebit(exchange, body);
            }
            else if(path.endsWith(ConfigUtil.loadProperty("balanceoperations.transfer.endpoint", "/transfer"))) {
                postTransfer(exchange, body);
            }
            else {
                ErrorResponseDTO error = new ErrorResponseDTO("Invalid transaction type");
                HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
            }
        }
    }

    private void postCredit(HttpExchange exchange, String body) throws Exception {
        CreditDebitRequestDTO request = parser.deserialise(body, CreditDebitRequestDTO.class);
        if(request.getAccountId() == null || request.getAmount() == null) {
            ErrorResponseDTO error = new ErrorResponseDTO("Missing required fields");
            HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
            return;
        }

        BalanceOperation operation = new BalanceOperation();
        operation.setAccountId(request.getAccountId());
        operation.setAmount(request.getAmount());
        operation.setType(BalanceOperation.Type.CREDIT);
        BalanceOperation created = service.credit(operation);
        BalanceOperationDTO response = new BalanceOperationDTO(created.getId(), created.getAccountId(),
                created.getToAccountId(), created.getAmount(), created.getType().name(), created.getTimestamp(),
                created.getRelatedBalanceOperationId());
        HttpClientService.sendResponse(exchange, 201, parser.serialise(response));

    }

    private void postDebit(HttpExchange exchange, String body) throws Exception {
        CreditDebitRequestDTO request = parser.deserialise(body, CreditDebitRequestDTO.class);
        if(request.getAccountId() == null || request.getAmount() == null) {
            ErrorResponseDTO error = new ErrorResponseDTO("Missing required fields");
            HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
            return;
        }

        BalanceOperation operation = new BalanceOperation();
        operation.setAccountId(request.getAccountId());
        operation.setAmount(request.getAmount());
        operation.setType(BalanceOperation.Type.DEBIT);
        BalanceOperation created = service.debit(operation);
        BalanceOperationDTO response = new BalanceOperationDTO(created.getId(), created.getAccountId(),
                created.getToAccountId(), created.getAmount(), created.getType().name(), created.getTimestamp(),
                created.getRelatedBalanceOperationId());
        HttpClientService.sendResponse(exchange, 201, parser.serialise(response));

    }

    private void postTransfer(HttpExchange exchange, String body) throws Exception {
        TransferRequestDTO request = parser.deserialise(body, TransferRequestDTO.class);
        if(request.getFromAccountId() == null || request.getToAccountId() == null || request.getAmount() == null) {
            ErrorResponseDTO error = new ErrorResponseDTO("Missing required fields");
            HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
            return;
        }

        BalanceOperation transfer = service.transfer(request.getFromAccountId(), request.getToAccountId(),
                request.getAmount());
        BalanceOperationDTO response = new BalanceOperationDTO(transfer.getId(), transfer.getAccountId(),
                transfer.getToAccountId(), transfer.getAmount(), transfer.getType().name(), transfer.getTimestamp(),
                transfer.getRelatedBalanceOperationId());
        HttpClientService.sendResponse(exchange, 201, parser.serialise(response));
    }
}
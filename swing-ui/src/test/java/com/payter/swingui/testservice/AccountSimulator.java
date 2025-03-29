// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.testservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AccountSimulator {

    private static final Map<String, Account> accountsDb = new HashMap<>();
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
        server.createContext("/accounts", new AccountHandler());
        server.start();
        System.out.println("Server is running on http://localhost:8000/");
    }

    static class AccountHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if(path.startsWith("/accounts") && method.equals("POST")) {
                if(path.matches("/accounts/\\w+/suspend")) {
                    suspendAccount(exchange);
                }
                else if(path.matches("/accounts/\\w+/reactivate")) {
                    reactivateAccount(exchange);
                }
                else if(path.matches("/accounts/\\w+/close")) {
                    closeAccount(exchange);
                }
                else {
                    createAccount(exchange);
                }
            }
            else if(path.matches("/accounts/\\w+/history") && method.equals("GET")) {
                getAccountHistory(exchange);
            }
            else if(path.matches("/accounts/\\w+") && method.equals("GET")) {
                getAccountDetails(exchange);
            }
            else {
                exchange.sendResponseHeaders(404, -1); // Not Found
            }
        }

        private void createAccount(HttpExchange exchange) throws IOException {
            String requestBody = getRequestBody(exchange);
            AccountRequest request = objectMapper.readValue(requestBody, AccountRequest.class);

            String accountName = request.getAccountName() != null ? request.getAccountName() : "";
            String currency = request.getCurrency() != null ? request.getCurrency() : "GBP";
            double initialBalance = request.getBalance() != null ? request.getBalance() : 0;

            if(!currency.equals("GBP") && !currency.equals("EUR") && !currency.equals("JPY")) {
                sendResponse(exchange, 400, "Invalid currency. Supported currencies are GBP, EUR, and JPY.");
                return;
            }

            String accountId = UUID.randomUUID().toString().split("-")[0];
            String creationTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Account newAccount = new Account(accountId, accountName, initialBalance, currency, "Active", creationTime);
            accountsDb.put(accountId, newAccount);

            sendResponse(exchange, 201, objectMapper.writeValueAsString(newAccount));
        }

        private void suspendAccount(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = accountsDb.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            if(account.getStatus().equals("Suspended")) {
                sendResponse(exchange, 400, "Account is already suspended");
                return;
            }

            account.setStatus("Suspended");
            account.addStatusHistory("Suspended");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String prettyJson = objectMapper.writeValueAsString(account);
            System.out.println("Account updated:\n" + prettyJson);

            sendResponse(exchange, 200, "Account suspended successfully");
        }

        private void reactivateAccount(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = accountsDb.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            if(account.getStatus().equals("Active")) {
                sendResponse(exchange, 400, "Account is already active");
                return;
            }

            account.setStatus("Active");
            account.addStatusHistory("Active");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String prettyJson = objectMapper.writeValueAsString(account);
            System.out.println("Account updated:\n" + prettyJson);

            sendResponse(exchange, 200, "Account reactivated successfully");
        }

        private void closeAccount(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = accountsDb.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            if(account.getStatus().equals("Closed")) {
                sendResponse(exchange, 400, "Account is already closed");
                return;
            }

            account.setStatus("Closed");
            account.addStatusHistory("Closed");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String prettyJson = objectMapper.writeValueAsString(account);
            System.out.println("Account updated:\n" + prettyJson);

            sendResponse(exchange, 200, "Account closed successfully");
        }

        private void getAccountDetails(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = accountsDb.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            AccountResponse response = new AccountResponse(account);
            sendResponse(exchange, 200, response.toJson());
        }

        private void getAccountHistory(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = accountsDb.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            ArrayNode history = objectMapper.createArrayNode();
            account.getStatusHistory().forEach(status -> history.add(status));

            sendResponse(exchange, 200, history.toString());
        }

        private String extractAccountId(HttpExchange exchange) {
            String path = exchange.getRequestURI().getPath();
            return path.split("/")[2];  // Extracts account ID from /accounts/{account_id}/... path
        }

        private String getRequestBody(HttpExchange exchange) throws IOException {
            try(InputStream inputStream = exchange.getRequestBody();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder requestBody = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
                String requestJson = requestBody.toString();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                Object jsonObject = objectMapper.readValue(requestJson, Object.class);
                String prettyJson = objectMapper.writeValueAsString(jsonObject);
                System.out.println("Received request:\n" + prettyJson);
                return requestJson;
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class AccountRequest {
        private String accountName;
        private String currency;
        private Double balance;

        public String getAccountName() {
            return accountName;
        }

        public String getCurrency() {
            return currency;
        }

        public Double getBalance() {
            return balance;
        }
    }

    static class AccountResponse {
        private String accountId;
        private double balance;
        private String currency;
        private String status;
        private List<String> statusHistory;
        private String creationTime;

        public AccountResponse(Account account) {
            this.accountId = account.getAccountId();
            this.balance = account.getBalance();
            this.currency = account.getCurrency();
            this.status = account.getStatus();
            this.statusHistory = account.getStatusHistory();
            this.creationTime = account.getCreationTime();
        }

        public String toJson() {
            ObjectNode responseNode = objectMapper.createObjectNode();
            responseNode.put("account_id", accountId);
            responseNode.put("balance", balance);
            responseNode.put("currency", currency);
            responseNode.put("status", status);
            responseNode.put("creation_time", creationTime);

            ArrayNode historyArray = objectMapper.createArrayNode();
            statusHistory.forEach(history -> historyArray.add(history));
            responseNode.set("status_history", historyArray);

            return responseNode.toString();
        }
    }

    static class Account {
        private String accountId;
        private String accountName;
        private double balance;
        private String status;
        private String currency;
        private String creationTime;
        private List<String> statusHistory = new ArrayList<>();

        public Account(String accountId, String accountName, double balance, String currency, String status,
                String creationTime) {
            this.accountId = accountId;
            this.accountName = accountName;
            this.balance = balance;
            this.currency = currency;
            this.status = status;
            this.creationTime = creationTime;
            addStatusHistory(status);
        }

        public String getAccountId() {
            return accountId;
        }

        public String getAccountName() {
            return accountName;
        }

        public double getBalance() {
            return balance;
        }

        public String getCurrency() {
            return currency;
        }

        public String getStatus() {
            return status;
        }

        public String getCreationTime() {
            return creationTime;
        }

        public List<String> getStatusHistory() {
            return statusHistory;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void addStatusHistory(String status) {
            statusHistory.add(status + " (" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ")");
        }
    }
}
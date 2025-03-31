// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.testservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AccountDatabaseServer {

    private static final ConcurrentHashMap<String, Account> ACCOUNT_DATABASE = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/accounts", new AccountHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Account Database Server running on http://localhost:8081/");
    }

    static class AccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            try {
                if(path.equals("/accounts") && method.equals("POST")) {
                    createAccount(exchange);
                }
                else if(path.matches("/accounts/\\w+/suspend") && method.equals("POST")) {
                    updateAccountStatus(exchange, "SUSPENDED");
                }
                else if(path.matches("/accounts/\\w+/reactivate") && method.equals("POST")) {
                    updateAccountStatus(exchange, "ACTIVE");
                }
                else if(path.matches("/accounts/\\w+/close") && method.equals("POST")) {
                    updateAccountStatus(exchange, "CLOSED");
                }
                else if(path.matches("/accounts/\\w+/balance") && method.equals("GET")) {
                    getBalance(exchange);
                }
                else if(path.matches("/accounts/\\w+/statushistory") && method.equals("GET")) {
                    getStatusHistory(exchange);
                }
                else if(path.matches("/accounts/\\w+/credit") && method.equals("POST")) {
                    credit(exchange);
                }
                else if(path.matches("/accounts/\\w+/debit") && method.equals("POST")) {
                    debit(exchange);
                }
                else if(path.matches("/accounts/\\w+/transfer/\\w+") && method.equals("POST")) {
                    transfer(exchange);
                }
                else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
            catch(IOException e) {
                System.err.println(e);
                throw e;
            }
        }

        private void createAccount(HttpExchange exchange) throws IOException {
            String requestBody;
            try(InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                    BufferedReader bufferedReader = new BufferedReader(reader)) {
                requestBody = bufferedReader.lines().collect(Collectors.joining());
            }

            ObjectNode requestJson = (ObjectNode) OBJECT_MAPPER.readTree(requestBody);

            String accountId = UUID.randomUUID().toString().split("-")[0];
            String accountName = requestJson.has("accountName") ? requestJson.get("accountName").asText() : "";
            String currency = requestJson.has("currency") ? requestJson.get("currency").asText() : "GBP";
            double balance = requestJson.has("balance") ? requestJson.get("balance").asDouble() : 0;

            if(!List.of("GBP", "EUR", "JPY").contains(currency)) {
                sendResponse(exchange, 400, "Invalid currency. Use GBP, EUR, or JPY.");
                return;
            }

            Account newAccount = new Account(accountId, accountName, currency, balance);
            ACCOUNT_DATABASE.put(accountId, newAccount);
            sendResponse(exchange, 201, OBJECT_MAPPER.writeValueAsString(newAccount));
        }

        private void updateAccountStatus(HttpExchange exchange, String newStatus) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = ACCOUNT_DATABASE.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            account.setStatus(newStatus);
            sendResponse(exchange, 200, "Account status updated to " + newStatus);
        }

        private void getBalance(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = ACCOUNT_DATABASE.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            sendResponse(exchange, 200, String.valueOf(account.getBalance()));
        }

        private void getStatusHistory(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = ACCOUNT_DATABASE.get(accountId);

            if(account == null) {
                sendResponse(exchange, 404, "Account not found");
                return;
            }

            sendResponse(exchange, 200, OBJECT_MAPPER.writeValueAsString(account.getStatusHistory()));
        }

        private void credit(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = ACCOUNT_DATABASE.get(accountId);

            if(account == null) {
                // Account not found
                sendResponse(exchange, 404, "false");
                return;
            }
            if(!account.getStatus().equals("ACTIVE")) {
                // Account is not active
                sendResponse(exchange, 400, "false");
                return;
            }

            String requestBody;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                requestBody = reader.lines().collect(Collectors.joining());
            }

            double amount = Double.valueOf(requestBody).doubleValue();
            if(amount <= 0) {
                // Amount must be positive
                sendResponse(exchange, 400, "false");
                return;
            }

            account.credit(amount);
            sendResponse(exchange, 200, "true");
        }

        private void debit(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            Account account = ACCOUNT_DATABASE.get(accountId);

            if(account == null) {
                // Account not found
                sendResponse(exchange, 404, "false");
                return;
            }
            if(!account.getStatus().equals("ACTIVE")) {
                // Account is not active
                sendResponse(exchange, 400, "false");
                return;
            }

            String requestBody;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                requestBody = reader.lines().collect(Collectors.joining());
            }

            double amount = Double.valueOf(requestBody).doubleValue();
            if(amount <= 0) {
                // Amount must be positive
                sendResponse(exchange, 400, "false");
                return;
            }
            if(account.getBalance() < amount) {
                // Insufficient funds
                sendResponse(exchange, 400, "false");
                return;
            }

            account.debit(amount);
            sendResponse(exchange, 200, "true");
        }

        private void transfer(HttpExchange exchange) throws IOException {
            String requestBody;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                requestBody = reader.lines().collect(Collectors.joining());
            }

            String fromAccountId = extractAccountId(exchange);
            String toAccountId = extractToAccountId(exchange);
            double amount = Double.valueOf(requestBody).doubleValue();

            Account fromAccount = ACCOUNT_DATABASE.get(fromAccountId);
            Account toAccount = ACCOUNT_DATABASE.get(toAccountId);

            if(fromAccount == null || toAccount == null) {
                // One or both accounts not found
                sendResponse(exchange, 404, "false");
                return;
            }
            if(!fromAccount.getStatus().equals("ACTIVE") || !toAccount.getStatus().equals("ACTIVE")) {
                // One or both accounts are not active
                sendResponse(exchange, 400, "false");
                return;
            }
            if(!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
                // Accounts must use the same currency
                sendResponse(exchange, 400, "false");
                return;
            }
            if(amount <= 0) {
                // Amount must be positive
                sendResponse(exchange, 400, "false");
                return;
            }
            if(fromAccount.getBalance() < amount) {
                // Insufficient funds in source account
                sendResponse(exchange, 400, "false");
                return;
            }

            synchronized(fromAccount) {
                synchronized(toAccount) {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }

            sendResponse(exchange, 200, "true");
        }

        private String extractAccountId(HttpExchange exchange) {
            return exchange.getRequestURI().getPath().split("/")[2];
        }

        private String extractToAccountId(HttpExchange exchange) {
            return exchange.getRequestURI().getPath().split("/")[4];
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class Account {
        private final String accountId;
        private final String accountName;
        private final String currency;
        private double balance;
        private String status;
        private final List<String> statusHistory = new ArrayList<>();

        public Account(String accountId, String accountName, String currency, double balance) {
            this.accountId = accountId;
            this.accountName = accountName;
            this.currency = currency;
            this.balance = balance;
            this.status = "ACTIVE";
            addStatusHistory("ACTIVE");
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

        public List<String> getStatusHistory() {
            return statusHistory;
        }

        public void setStatus(String newStatus) {
            this.status = newStatus;
            addStatusHistory(newStatus);
        }

        public synchronized void credit(double amount) {
            this.balance += amount;
        }

        public synchronized void debit(double amount) {
            this.balance -= amount;
        }

        private void addStatusHistory(String status) {
            statusHistory.add(status + " (" + new Date() + ")");
        }
    }
}
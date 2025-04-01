// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.testservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class BalanceOperationsSimulator {

    private static final String ACCOUNT_DATABASE_URL = "http://localhost:8081/accounts";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
        server.createContext("/balance", new BalanceHandler());
        server.start();
        System.out.println("Balance Operations Server is running on http://localhost:8001/");
    }

    static class BalanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if(path.matches("/balance/\\w+") && method.equals("GET")) {
                getBalance(exchange);
            }
            else if(path.matches("/balance/\\w+/credit") && method.equals("POST")) {
                credit(exchange);
            }
            else if(path.matches("/balance/\\w+/debit") && method.equals("POST")) {
                debit(exchange);
            }
            else if(path.matches("/balance/\\w+/transfer/\\w+") && method.equals("POST")) {
                transfer(exchange);
            }
            else {
                exchange.sendResponseHeaders(404, -1);
            }
        }

        private void getBalance(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            String response = sendHttpRequest(ACCOUNT_DATABASE_URL + "/" + accountId + "/balance", "GET", null);
            sendResponse(exchange, 200, response);
        }

        private void credit(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);

            String requestBody;
            try(InputStream inputStream = exchange.getRequestBody()) {
                requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            String response = sendHttpRequest(ACCOUNT_DATABASE_URL + "/" + accountId + "/credit", "POST", requestBody);
            sendResponse(exchange, 200, response);
        }

        private void debit(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);

            String requestBody;
            try(InputStream inputStream = exchange.getRequestBody()) {
                requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            String response = sendHttpRequest(ACCOUNT_DATABASE_URL + "/" + accountId + "/debit", "POST", requestBody);
            sendResponse(exchange, 200, response);
        }

        private void transfer(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            String toAccountId = extractToAccountId(exchange);

            String amount;
            try(InputStream inputStream = exchange.getRequestBody()) {
                amount = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            String response = sendHttpRequest(ACCOUNT_DATABASE_URL + "/" + accountId + "/transfer/" + toAccountId,
                    "POST", amount);
            sendResponse(exchange, 200, response);
        }

        private String extractAccountId(HttpExchange exchange) {
            return exchange.getRequestURI().getPath().split("/")[2];
        }

        private String extractToAccountId(HttpExchange exchange) {
            return exchange.getRequestURI().getPath().split("/")[4];
        }

        private String sendHttpRequest(String url, String method, String body) throws IOException {
            try {
                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))
                        .header("Content-Type", "application/json");

                if(method.equals("POST")) {
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
                }
                else {
                    requestBuilder.GET();
                }

                HttpRequest request = requestBuilder.build();
                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            }
            catch(Exception e) {
                throw new IOException("Failed to send HTTP request", e);
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
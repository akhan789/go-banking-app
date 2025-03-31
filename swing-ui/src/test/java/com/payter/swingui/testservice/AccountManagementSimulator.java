// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.testservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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
public class AccountManagementSimulator {

    private static final String ACCOUNT_DATABASE_URL = "http://localhost:8081/accounts";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/accounts", new AccountHandler());
        server.start();
        System.out.println("Account Management Server running on http://localhost:8000/");
    }

    static class AccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if(path.equals("/accounts") && method.equals("POST")) {
                createAccount(exchange);
            }
            else if(path.matches("/accounts/\\w+/suspend") && method.equals("POST")) {
                updateStatus(exchange, "suspend");
            }
            else if(path.matches("/accounts/\\w+/reactivate") && method.equals("POST")) {
                updateStatus(exchange, "reactivate");
            }
            else if(path.matches("/accounts/\\w+/close") && method.equals("POST")) {
                updateStatus(exchange, "close");
            }
            else if(path.matches("/accounts/\\w+/history") && method.equals("GET")) {
                getStatusHistory(exchange);
            }
            else {
                exchange.sendResponseHeaders(404, -1);
            }
        }

        private void createAccount(HttpExchange exchange) throws IOException {
            String requestBody;
            try(InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                    BufferedReader bufferedReader = new BufferedReader(reader)) {
                requestBody = bufferedReader.lines().collect(Collectors.joining());
            }
            String response = sendHttpRequest(ACCOUNT_DATABASE_URL, "POST", requestBody);
            sendResponse(exchange, 201, response);
        }


        private void updateStatus(HttpExchange exchange, String action) throws IOException {
            String accountId = extractAccountId(exchange);
            String response = sendHttpRequest(ACCOUNT_DATABASE_URL + "/" + accountId + "/" + action, "POST", "");
            sendResponse(exchange, 200, response);
        }

        private void getStatusHistory(HttpExchange exchange) throws IOException {
            String accountId = extractAccountId(exchange);
            String response = sendHttpRequest(ACCOUNT_DATABASE_URL + "/" + accountId + "/statushistory", "GET", "");
            sendResponse(exchange, 200, response);
        }

        private String extractAccountId(HttpExchange exchange) {
            return exchange.getRequestURI().getPath().split("/")[2];
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private String sendHttpRequest(String url, String method, String requestBody) throws IOException {
            try {
                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .method(method, requestBody.isEmpty() ? HttpRequest.BodyPublishers.noBody()
                                : HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8));

                HttpResponse<String> response = HTTP_CLIENT.send(requestBuilder.build(),
                        HttpResponse.BodyHandlers.ofString());
                return response.body();
            }
            catch(Exception e) {
                throw new IOException("Error communicating with account database", e);
            }
        }
    }
}
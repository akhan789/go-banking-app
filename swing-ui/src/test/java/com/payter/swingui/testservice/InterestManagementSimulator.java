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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payter.swingui.model.AuditLoggingEntry;
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
public class InterestManagementSimulator {

    private static final String ACCOUNT_DATABASE_URL = "http://localhost:8081/accounts";
    private static final String AUDIT_LOG_URL = "http://localhost:8003/audit/log";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER;
    private static double globalDailyRate = 4.5;
    private static InterestConfig interestConfig;
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) throws Exception {
        if(interestConfig == null) {
            interestConfig = new InterestConfig("MONTHLY", LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        }

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8002), 0);
        server.createContext("/interest", new InterestHandler());
        server.start();
        System.out.println("Interest Management Simulator running on http://localhost:8002/");

        SCHEDULER.scheduleAtFixedRate(InterestManagementSimulator::applyScheduledInterest, 0, 24, TimeUnit.HOURS);
    }

    static class InterestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            try {
                if(path.equals("/interest/rate") && method.equals("POST")) {
                    setInterestRate(exchange);
                }
                if(path.equals("/interest/rate") && method.equals("GET")) {
                    getGlobalDailyRate(exchange);
                }
                else if(path.equals("/interest/frequency") && method.equals("POST")) {
                    setCalculationFrequency(exchange);
                }
                else if(path.equals("/interest/frequency") && method.equals("GET")) {
                    getCalculationFrequency(exchange);
                }
                else if(path.equals("/interest/apply") && method.equals("POST")) {
                    applyInterest(exchange);
                }
                else if(path.equals("/interest/skip-time") && method.equals("POST")) {
                    skipTime(exchange);
                }
                else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
            catch(

            IOException e) {
                System.err.println(e);
                throw e;
            }
        }

        private void setInterestRate(HttpExchange exchange) throws IOException {
            String rate;
            try(InputStream inputStream = exchange.getRequestBody()) {
                rate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            if(rate == null || rate.isEmpty()) {
                sendResponse(exchange, 400, "No rate supplied");
                return;
            }

            double dailyRate = Double.valueOf(rate).doubleValue();
            if(dailyRate < 0) {
                sendResponse(exchange, 400, "Daily rate cannot be negative");
                return;
            }

            globalDailyRate = dailyRate;
            logAuditEvent("INTEREST_RATE_CHANGE", "Global daily rate set to " + dailyRate);
            sendResponse(exchange, 200, rate);
        }

        private void getGlobalDailyRate(HttpExchange exchange) throws IOException {
            sendResponse(exchange, 200, String.valueOf(globalDailyRate));
        }

        private void setCalculationFrequency(HttpExchange exchange) throws IOException {
            String calculationFrequency;
            try(InputStream inputStream = exchange.getRequestBody()) {
                calculationFrequency = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            if(!"DAILY|WEEKLY|MONTHLY".contains(calculationFrequency.toUpperCase())) {
                sendResponse(exchange, 400, "Invalid frequency. Use DAILY, WEEKLY, or MONTHLY.");
                return;
            }

            interestConfig = new InterestConfig(calculationFrequency, LocalDateTime.now(),
                    calculateNextApplicationTime(calculationFrequency));
            sendResponse(exchange, 200, OBJECT_MAPPER.writeValueAsString(interestConfig));
            logAuditEvent("INTEREST_CALCULATION_FREQUENCY",
                    "Interest calculation frequency updated: " + calculationFrequency);
        }

        private void getCalculationFrequency(HttpExchange exchange) throws IOException {
            sendResponse(exchange, 200, interestConfig.getFrequency());
        }

        private void applyInterest(HttpExchange exchange) throws IOException {
            try {
                String forceApplyInterest;
                try(InputStream inputStream = exchange.getRequestBody()) {
                    forceApplyInterest = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
                if(forceApplyInterest != null && !forceApplyInterest.isEmpty()) {
                    boolean force = Boolean.valueOf(forceApplyInterest).booleanValue();
                    applyInterestToAccounts(force);
                }
                else {
                    applyInterestToAccounts(false);
                }
                interestConfig.setLastAppliedAt(LocalDateTime.now());
                interestConfig.setNextApplicationAt(calculateNextApplicationTime(interestConfig.getFrequency()));
                sendResponse(exchange, 200, "Interest applied successfully");
            }
            catch(Exception e) {
                sendResponse(exchange, 500, "Failed to apply interest: " + e.getMessage());
            }
        }

        private void skipTime(HttpExchange exchange) throws IOException {
            String periodsToSkip;
            try(InputStream inputStream = exchange.getRequestBody()) {
                periodsToSkip = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
            int periods = Integer.valueOf(periodsToSkip).intValue();

            // Simulate time skip by adjusting timestamps
            LocalDateTime now = LocalDateTime.now();
            // Set last application before skip
            interestConfig.setLastAppliedAt(now.minusDays(periods));
            // Set next application in the past
            interestConfig.setNextApplicationAt(now.minusDays(1));
            // Force immediate check
            applyScheduledInterest();

            sendResponse(exchange, 200, "Time skipped by " + periods + " periods, interest applied if due");
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private static void applyScheduledInterest() {
        try {
            if(interestConfig == null || globalDailyRate == 0.0) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(interestConfig.getNextApplicationAt())) {
                applyInterestToAccounts(false);
                interestConfig.setLastAppliedAt(now);
                interestConfig.setNextApplicationAt(calculateNextApplicationTime(interestConfig.getFrequency()));
            }
        }
        catch(Exception e) {
            System.err.println("Error applying scheduled interest: " + e.getMessage());
        }
    }

    private static void applyInterestToAccounts(boolean force) throws IOException {
        String accountsJson = sendHttpRequest(ACCOUNT_DATABASE_URL, "GET", null);
        List<Account> accounts = OBJECT_MAPPER.readValue(accountsJson,
                OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Account.class));

        // Calculate the interest based on a global daily rate
        // and credit to each qualifying account. So if no days
        // have passed there is no interest applied to the accounts
        // unless force apply was passed in which case immediately apply
        // a flat rate based on globalDailyRate.
        int days = (int) ChronoUnit.DAYS.between(interestConfig.getLastAppliedAt(), LocalDateTime.now());
        for(Account account : accounts) {
            if("ACTIVE".equals(account.getStatus())) {
                double interest = force ? calculateInterestForced(account.getBalance(), globalDailyRate)
                        : calculateInterest(account.getBalance(), globalDailyRate, days);
                sendHttpRequest(ACCOUNT_DATABASE_URL + "/" + account.getAccountId() + "/credit", "POST",
                        String.valueOf(interest));
                logAuditEvent("TRANSACTION", "Credited " + interest + " to Account " + account.getAccountId());
            }
        }
    }

    private static double calculateInterest(double balance, double dailyRate, int days) {
        return balance * (dailyRate / 100) * days;
    }

    private static double calculateInterestForced(double balance, double dailyRate) {
        return balance * (dailyRate / 100);
    }

    private static LocalDateTime calculateNextApplicationTime(String frequency) {
        LocalDateTime now = LocalDateTime.now();
        switch(frequency.toUpperCase()) {
            case "DAILY":
                return now.plusDays(1);
            case "WEEKLY":
                return now.plusWeeks(1);
            case "MONTHLY":
                return now.plusMonths(1);
            default:
                return now.plusMonths(1);
        }
    }

    private static void logAuditEvent(String eventType, String details) {
        try {
            String requestBody = OBJECT_MAPPER
                    .writeValueAsString(new AuditLoggingEntry(eventType, details, LocalDateTime.now()));
            sendHttpRequest(AUDIT_LOG_URL, "POST", requestBody);
        }
        catch(Exception e) {
            System.err.println("Failed to log audit event: " + e.getMessage());
        }
    }

    private static String sendHttpRequest(String url, String method, String body) throws IOException {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type",
                    "application/json");

            if("POST".equals(method)) {
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
            }
            else {
                requestBuilder.GET();
            }

            HttpResponse<String> response = HTTP_CLIENT.send(requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch(Exception e) {
            throw new IOException("Failed to send HTTP request", e);
        }
    }

    static class InterestConfig {
        private String frequency;
        private LocalDateTime lastAppliedAt;
        private LocalDateTime nextApplicationAt;

        public InterestConfig(String frequency, LocalDateTime lastAppliedAt, LocalDateTime nextApplicationAt) {
            this.frequency = frequency;
            this.lastAppliedAt = lastAppliedAt;
            this.nextApplicationAt = nextApplicationAt;
        }

        public String getFrequency() {
            return frequency;
        }

        public LocalDateTime getLastAppliedAt() {
            return lastAppliedAt;
        }

        public LocalDateTime getNextApplicationAt() {
            return nextApplicationAt;
        }

        public void setLastAppliedAt(LocalDateTime lastAppliedAt) {
            this.lastAppliedAt = lastAppliedAt;
        }

        public void setNextApplicationAt(LocalDateTime nextApplicationAt) {
            this.nextApplicationAt = nextApplicationAt;
        }
    }

    static class Account {
        private String accountId;
        private String accountName;
        private String currency;
        private double balance;
        private String status;

        public String getAccountId() {
            return accountId;
        }

        public String getAccountName() {
            return accountName;
        }

        public String getCurrency() {
            return currency;
        }

        public double getBalance() {
            return balance;
        }

        public String getStatus() {
            return status;
        }
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.testservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class AuditLoggingDatabaseSimulator {

    private static final List<AuditLoggingEntry> AUDIT_LOGS = new ArrayList<>();
    private static long nextId = 0;
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8003), 0);
        server.createContext("/audit", new AuditHandler());
        server.start();
        System.out.println("Audit Logging Database Simulator running on http://localhost:8003/");
    }

    static class AuditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if(path.equals("/audit/log") && method.equals("POST")) {
                writeLogEntry(exchange);
            }
            else if(path.startsWith("/audit/logs") && method.equals("GET")) {
                getLogs(exchange);
            }
            else {
                exchange.sendResponseHeaders(404, -1);
            }
        }

        private void writeLogEntry(HttpExchange exchange) throws IOException {
            String requestBody;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                requestBody = reader.lines().collect(Collectors.joining());
            }
            AuditLoggingEntry entry = OBJECT_MAPPER.readValue(requestBody, AuditLoggingEntry.class);
            AuditLoggingEntry entryWithId = new AuditLoggingEntry(nextId++, entry.getEventType(), entry.getDetails(),
                    entry.getTimestamp());
            synchronized(AUDIT_LOGS) {
                AUDIT_LOGS.add(entryWithId);
            }
            sendResponse(exchange, 200, "Log added, id:" + entryWithId.getId());
        }

        private void getLogs(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            long afterId = query != null && query.startsWith("after=") ? Long.parseLong(query.split("=")[1]) : -1;
            synchronized(AUDIT_LOGS) {
                //@formatter:off
                List<AuditLoggingEntry> filtered = 
                    AUDIT_LOGS.stream()
                        .filter(log -> log.getId() > afterId)
                        .collect(Collectors.toList());
                //@formatter:on
                sendResponse(exchange, 200, OBJECT_MAPPER.writeValueAsString(filtered));
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
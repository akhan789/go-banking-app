// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.payter.common.auth.Authenticator;
import com.payter.common.dto.auditlogging.AuditLoggingRequestDTO;
import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.service.auditlogging.entity.AuditLogging;
import com.payter.service.auditlogging.entity.AuditLogging.EventType;
import com.payter.service.auditlogging.service.AuditLoggingService;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AuditLoggingController {

    private final Authenticator authenticator;
    private final AuditLoggingService service;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public AuditLoggingController(Authenticator authenticator, AuditLoggingService service) {
        this.authenticator = authenticator;
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
            if(!authenticator.isValidApiKey(apiKey)) {
                ErrorResponseDTO error = new ErrorResponseDTO("Unauthorized - Invalid or missing API key");
                HttpClientService.sendResponse(exchange, 401, parser.serialise(error));
                return;
            }

            switch(method) {
                case "POST":
                    handlePost(exchange);
                    break;
                case "GET":
                    handleGet(exchange);
                    break;
                default:
                    ErrorResponseDTO error = new ErrorResponseDTO("Method Not Allowed");
                    HttpClientService.sendResponse(exchange, 405, parser.serialise(error));
            }
        }
        catch(Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage());
            try {
                HttpClientService.sendResponse(exchange, 500, parser.serialise(error));
            }
            catch(Exception e1) {
                throw new IOException(e1);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            AuditLoggingRequestDTO request = parser.deserialise(body, AuditLoggingRequestDTO.class);
            if(request.getEventType() == null) {
                ErrorResponseDTO error = new ErrorResponseDTO("Missing required field: eventType");
                HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                return;
            }
            if(request.getDetails() == null) {
                ErrorResponseDTO error = new ErrorResponseDTO("Missing required field: details");
                HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                return;
            }
            service.log(mapEventType(request.getEventType()), request.getDetails());
            HttpClientService.sendResponse(exchange, 204, "");
        }
    }

    private void handleGet(HttpExchange exchange) throws Exception {
        String query = exchange.getRequestURI().getQuery();
        long afterId = query != null && query.startsWith("after=") ? Long.parseLong(query.split("=")[1]) : -1;
        List<AuditLoggingRequestDTO> filtered = mapToAuditLoggingRequestDTO(service.getLogs(afterId));
        if(filtered.size() == 0) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        HttpClientService.sendResponse(exchange, 200, parser.serialise(filtered));
    }

    private EventType mapEventType(AuditLoggingRequestDTO.EventType dtoEventType) {
        return switch(dtoEventType) {
            case INFO -> EventType.INFO;
            case WARNING -> EventType.WARNING;
            case ERROR -> EventType.ERROR;
            case CREATE -> EventType.CREATE;
            case READ -> EventType.READ;
            case UPDATE -> EventType.UPDATE;
            case DELETE -> EventType.DELETE;
            default -> throw new IllegalArgumentException("Unexpected value: " + dtoEventType);
        };
    }

    private List<AuditLoggingRequestDTO> mapToAuditLoggingRequestDTO(List<AuditLogging> logs) {
        //@formatter:off
        return logs.stream()
                .map(log -> new AuditLoggingRequestDTO(log.getId(), AuditLoggingRequestDTO.EventType.valueOf(log.getEventType().name()), log.getDetails(), log.getTimestamp()))
                .collect(Collectors.toList());
        //@formatter:on
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.payter.common.auth.Authenticator;
import com.payter.common.dto.auditlogging.AuditLoggingRequestDTO;
import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
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
            if(request.getMessage() == null) {
                ErrorResponseDTO error = new ErrorResponseDTO("Missing required field: message");
                HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                return;
            }
            service.log(request.getMessage());
            HttpClientService.sendResponse(exchange, 204, "");
        }
    }
}
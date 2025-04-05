// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.controller;

import java.io.IOException;
import java.io.InputStream;

import com.payter.common.auth.Authenticator;
import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.dto.interestmanagement.InterestManagementDTO;
import com.payter.common.dto.interestmanagement.InterestManagementRequestDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.ConfigUtil;
import com.payter.service.interestmanagement.entity.InterestManagement;
import com.payter.service.interestmanagement.entity.InterestManagement.CalculationFrequency;
import com.payter.service.interestmanagement.service.InterestManagementService;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class InterestManagementController {

    private final Authenticator authenticator;
    private final InterestManagementService service;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public InterestManagementController(Authenticator authenticator, InterestManagementService service) {
        this.authenticator = authenticator;
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
            if(!authenticator.isValidApiKey(apiKey)) {
                ErrorResponseDTO error = new ErrorResponseDTO("Unauthorized - Invalid or missing API key");
                HttpClientService.sendResponse(exchange, 401, parser.serialise(error));
                return;
            }

            switch(method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange, path);
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

    private void handleGet(HttpExchange exchange, String path) throws Exception {
        if(path.equals(ConfigUtil.loadProperty("interestmanagement.endpoint", "/interestmanagement"))) {
            InterestManagement latest = service.getLatestInterestManagement();
            InterestManagementDTO response = new InterestManagementDTO(latest.getId(), latest.getDailyRate(),
                    latest.getCalculationFrequency().name(), latest.getCreatedAt());
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
    }

    private void handlePost(HttpExchange exchange, String path) throws Exception {
        if(path.equals(ConfigUtil.loadProperty("interestmanagement.endpoint", "/interestmanagement"))) {
            try(InputStream requestBody = exchange.getRequestBody()) {
                String body = new String(requestBody.readAllBytes());
                InterestManagementRequestDTO request = parser.deserialise(body, InterestManagementRequestDTO.class);
                if(request.getDailyRate() == null || request.getCalculationFrequency() == null) {
                    ErrorResponseDTO error = new ErrorResponseDTO("Missing required fields");
                    HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                    return;
                }

                CalculationFrequency calcalationFrequency = CalculationFrequency
                        .valueOf(request.getCalculationFrequency().toUpperCase());
                InterestManagement updated = service.configureInterest(request.getDailyRate(), calcalationFrequency);
                InterestManagementDTO response = new InterestManagementDTO(updated.getId(), updated.getDailyRate(),
                        updated.getCalculationFrequency().name(), updated.getCreatedAt());
                HttpClientService.sendResponse(exchange, 201, parser.serialise(response));
            }
        }
    }
}
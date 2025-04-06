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
import com.payter.common.util.Util;
import com.payter.service.interestmanagement.entity.InterestManagement;
import com.payter.service.interestmanagement.entity.InterestManagement.CalculationFrequency;
import com.payter.service.interestmanagement.service.InterestManagementService;
import com.sun.net.httpserver.HttpExchange;

/**
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
        String endpoint = ConfigUtil.loadProperty("interestmanagement.endpoint", "/interestmanagement");
        InterestManagement latest = service.getLatestInterestManagement();
        InterestManagementDTO response = new InterestManagementDTO(latest.getId(), latest.getDailyRate(),
                latest.getCalculationFrequency().name(), latest.getCreatedAt());
        if(path.equals(endpoint + "/rate")) {
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
        else if(path.equals(endpoint + "/calculationfrequency")) {
            HttpClientService.sendResponse(exchange, 200, parser.serialise(response));
        }
        else {
            ErrorResponseDTO error = new ErrorResponseDTO("Invalid request");
            HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
        }
    }

    private void handlePost(HttpExchange exchange, String path) throws Exception {
        String endpoint = ConfigUtil.loadProperty("interestmanagement.endpoint", "/interestmanagement");
        if(path.equals(endpoint + "/rate")) {
            try(InputStream requestBody = exchange.getRequestBody()) {
                String body = new String(requestBody.readAllBytes());
                InterestManagementRequestDTO request = parser.deserialise(body, InterestManagementRequestDTO.class);
                if(request.getDailyRate() == null) {
                    ErrorResponseDTO error = new ErrorResponseDTO("Missing dailyRate field");
                    HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                    return;
                }
                InterestManagement updated = service.configureInterest(request.getDailyRate(), null);
                InterestManagementDTO response = new InterestManagementDTO(updated.getId(), updated.getDailyRate(),
                        updated.getCalculationFrequency().name(), updated.getCreatedAt());
                HttpClientService.sendResponse(exchange, 201, parser.serialise(response));
            }
        }
        else if(path.equals(endpoint + "/calculationfrequency")) {
            try(InputStream requestBody = exchange.getRequestBody()) {
                String body = new String(requestBody.readAllBytes());
                InterestManagementRequestDTO request = parser.deserialise(body, InterestManagementRequestDTO.class);
                if(request.getCalculationFrequency() == null) {
                    ErrorResponseDTO error = new ErrorResponseDTO("Missing calculationFrequency field");
                    HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                    return;
                }
                CalculationFrequency calculationFrequency = CalculationFrequency
                        .valueOf(request.getCalculationFrequency().toUpperCase());
                InterestManagement updated = service.configureInterest(null, calculationFrequency);
                InterestManagementDTO response = new InterestManagementDTO(updated.getId(), updated.getDailyRate(),
                        updated.getCalculationFrequency().name(), updated.getCreatedAt());
                HttpClientService.sendResponse(exchange, 201, parser.serialise(response));
            }
        }
        else if(path.equals(endpoint + "/apply")) {
            String query = exchange.getRequestURI().getQuery();
            boolean force = Boolean.parseBoolean(Util.getQueryParam(query, "force"));
            service.applyInterest(force);
            HttpClientService.sendResponse(exchange, 200, parser.serialise("Interest applied successfully"));
        }
        else if(path.equals(endpoint + "/skip-time")) {
            try(InputStream requestBody = exchange.getRequestBody()) {
                String body = new String(requestBody.readAllBytes());
                Integer periodsToSkip = parser.deserialise(body, Integer.class);
                if(periodsToSkip == null || periodsToSkip < 0) {
                    ErrorResponseDTO error = new ErrorResponseDTO("Invalid or missing periodsToSkip value");
                    HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                    return;
                }
                service.skipTime(periodsToSkip);
                HttpClientService.sendResponse(exchange, 200, parser.serialise("Time skipped successfully"));
            }
        }
        else if(path.equals(endpoint)) {
            try(InputStream requestBody = exchange.getRequestBody()) {
                String body = new String(requestBody.readAllBytes());
                InterestManagementRequestDTO request = parser.deserialise(body, InterestManagementRequestDTO.class);
                if(request.getDailyRate() == null || request.getCalculationFrequency() == null) {
                    ErrorResponseDTO error = new ErrorResponseDTO("Missing required fields");
                    HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
                    return;
                }
                CalculationFrequency calculationFrequency = CalculationFrequency
                        .valueOf(request.getCalculationFrequency().toUpperCase());
                InterestManagement updated = service.configureInterest(request.getDailyRate(), calculationFrequency);
                InterestManagementDTO response = new InterestManagementDTO(updated.getId(), updated.getDailyRate(),
                        updated.getCalculationFrequency().name(), updated.getCreatedAt());
                HttpClientService.sendResponse(exchange, 201, parser.serialise(response));
            }
        }
        else {
            ErrorResponseDTO error = new ErrorResponseDTO("Invalid request");
            HttpClientService.sendResponse(exchange, 400, parser.serialise(error));
        }
    }
}
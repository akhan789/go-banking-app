// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.controller;

import java.io.IOException;
import java.io.InputStream;

import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.service.interestmanagement.entity.InterestManagement;
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

    private final InterestManagementService service;
    private static final String VALID_API_KEY = "api-key-12345";

    public InterestManagementController(InterestManagementService service) {
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
            if(!VALID_API_KEY.equals(apiKey)) {
                HttpClientService.sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if(path.equals("/interestmanagement")) {
                Parser parser = ParserFactory.getParser(ParserType.JSON);
                switch(method) {
                    case "POST":
                        try(InputStream requestBody = exchange.getRequestBody()) {
                            String body = new String(requestBody.readAllBytes());
                            InterestManagement request = parser.deserialise(body, InterestManagement.class);
                            InterestManagement updated = service.configureInterest(request.getDailyRate(),
                                    request.getCalculationFrequency());
                            HttpClientService.sendResponse(exchange, 201, parser.serialise(updated));
                        }
                        break;
                    case "GET":
                        InterestManagement latest = service.getLatestInterestManagement(); // Direct access for simplicity
                        HttpClientService.sendResponse(exchange, 200, parser.serialise(latest));
                        break;
                    default:
                        HttpClientService.sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                }
            }
            else {
                HttpClientService.sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }
        }
        catch(Exception e) {
            HttpClientService.sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
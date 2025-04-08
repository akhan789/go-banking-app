// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway.controller;

import java.io.InputStream;

import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.service.gateway.service.GatewayService;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class GatewayController {

    private final GatewayService service;
    private final Parser parser = ParserFactory.getParser(ParserType.JSON);

    public GatewayController(GatewayService service) {
        this.service = service;
    }

    public void handle(HttpExchange exchange) {
        try {
            String fullPath = exchange.getRequestURI().getRawPath();
            String query = exchange.getRequestURI().getRawQuery();
            if(query != null) {
                fullPath += "?" + query;
            }

            String method = exchange.getRequestMethod();
            String body = null;
            if("POST".equals(method) || "PUT".equals(method)) {
                try(InputStream inputStream = exchange.getRequestBody()) {
                    body = new String(inputStream.readAllBytes());
                }
            }

            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
            String response = service.forwardRequest(fullPath, method, body, apiKey);
            HttpClientService.sendResponse(exchange, getStatusCode(method, response), response);
        }
        catch(Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage());
            String errorResponse = null;
            try {
                errorResponse = parser.serialise(error);
                HttpClientService.sendResponse(exchange, 500, errorResponse);
            }
            catch(Exception e1) {
                System.err.println("ErrorResponse could not be serialised:\n" + errorResponse);
            }
        }
    }

    private int getStatusCode(String method, String response) throws Exception {
        if(response.contains("error")) {
            if(response.contains("Unauthorized")) {
                return 401;
            }
            return 400;
        }
        switch(method) {
            case "POST":
                return 201;
            case "GET":
            case "PUT":
                return 200;
            case "DELETE":
                return 204;
            default:
                ErrorResponseDTO error = new ErrorResponseDTO("Method Not Allowed");
                throw new Exception(parser.serialise(error));
        }
    }
}
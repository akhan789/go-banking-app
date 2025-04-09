// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payter.common.dto.gateway.ErrorResponseDTO;
import com.payter.common.util.ConfigUtil;

/**
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public abstract class AbstractHttpClient {

    private static final Logger LOG = LogManager.getLogger(AbstractHttpClient.class);
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    protected String getBaseUrl() {
        return ConfigUtil.loadProperty("service.gateway.baseUrl", "http://localhost:8000");
    }

    protected <T> T sendGetRequest(String endpoint, Class<T> responseType) throws Exception {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBaseUrl() + endpoint))
            .GET()
            .header("Content-Type", "application/json")
            .header("X-API-Key", ConfigUtil.loadProperty("service.gateway.apiKey", "default_api_key"))
            .build();
        //@formatter:on
        LOG.info("Sending request (no body): headers: " + request.headers() + ": " + request);
        return handleResponse(request, responseType);
    }

    protected <T> T sendGetRequest(String endpoint, TypeReference<T> typeReference) throws Exception {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBaseUrl() + endpoint))
            .GET()
            .header("Content-Type", "application/json")
            .header("X-API-Key", ConfigUtil.loadProperty("service.gateway.apiKey", "default_api_key"))
            .build();
        //@formatter:on
        LOG.info("Sending request (no body): headers: " + request.headers() + ": " + request);
        return handleResponse(request, typeReference);
    }

    protected <T> T sendPostRequest(String endpoint, Object requestBody, Class<T> responseType) throws Exception {
        String jsonBody = requestBody != null ? convertToJSONString(requestBody) : "";
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBaseUrl() + endpoint))
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .header("Content-Type", "application/json")
            .header("X-API-Key", ConfigUtil.loadProperty("service.gateway.apiKey", "default_api_key"))
            .build();
        //@formatter:on
        LOG.info("Sending request: headers: " + request.headers() + ": " + request + ": "
                + (jsonBody != null && !jsonBody.isEmpty() ? jsonBody : "<no body>"));
        return handleResponse(request, responseType);
    }

    protected <T> T sendPutRequest(String endpoint, Object requestBody, Class<T> responseType) throws Exception {
        String jsonBody = requestBody != null ? convertToJSONString(requestBody) : "";
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBaseUrl() + endpoint))
            .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
            .header("Content-Type", "application/json")
            .header("X-API-Key", ConfigUtil.loadProperty("service.gateway.apiKey", "default_api_key"))
            .build();
        //@formatter:on
        LOG.info("Sending request: " + request.headers() + ": " + request + ": "
                + (jsonBody != null && !jsonBody.isEmpty() ? jsonBody : "<no body>"));
        return handleResponse(request, responseType);
    }

    private <T> T handleResponse(HttpRequest request, Class<T> responseType) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if(response.body() != null && !response.body().isEmpty()) {
            LOG.info("Received response: " + response.body());
        }
        else {
            LOG.info("Received response no body");
        }
        if(statusCode >= 200 && statusCode < 300) {
            if(responseType == Void.class || response.body() == null || response.body().isEmpty()) {
                return null;
            }
            return OBJECT_MAPPER.readValue(response.body(), responseType);
        }
        else {
            ErrorResponseDTO error = OBJECT_MAPPER.readValue(response.body(), ErrorResponseDTO.class);
            throw new RuntimeException("Request failed with status " + statusCode + ": " + error.getError());
        }
    }

    private <T> T handleResponse(HttpRequest request, TypeReference<T> typeReference) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if(response.body() != null && !response.body().isEmpty()) {
            LOG.info("Received response: " + response.body());
        }
        else {
            LOG.info("Received response no body");
        }
        if(statusCode >= 200 && statusCode < 300) {
            if(response.body() == null || response.body().isEmpty()) {
                return null;
            }
            return OBJECT_MAPPER.readValue(response.body(), typeReference);
        }
        else {
            ErrorResponseDTO error = OBJECT_MAPPER.readValue(response.body(), ErrorResponseDTO.class);
            throw new RuntimeException("Request failed with status " + statusCode + ": " + error.getError());
        }
    }

    protected <T> String convertToJSONString(T object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }
}
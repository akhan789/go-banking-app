// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payter.common.util.ConfigUtil;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public abstract class AbstractHttpClient {

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

    @SuppressWarnings("unchecked")
    public <T> T sendGetRequest(String endpoint, Class<T> responseType) throws Exception {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBaseUrl() + endpoint))
            .GET()
            .header("Content-Type", "application/json")
            .build();
        //@formatter:on
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() >= 200 && response.statusCode() < 300) {
            try {
                return OBJECT_MAPPER.readValue(response.body(), responseType);
            }
            catch(Exception e) {
                return (T) response.body();
            }
        }
        else {
            throw new RuntimeException(
                    "GET request failed with status: " + response.statusCode() + " - " + response.body());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T sendGetRequest(String endpoint, TypeReference<T> typeReference) throws Exception {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBaseUrl() + endpoint))
            .GET()
            .header("Content-Type", "application/json")
            .build();
        //@formatter:on
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() >= 200 && response.statusCode() < 300) {
            try {
                return OBJECT_MAPPER.readValue(response.body(), typeReference);
            }
            catch(Exception e) {
                return (T) response.body();
            }
        }
        else {
            throw new RuntimeException(
                    "GET request failed with status: " + response.statusCode() + " - " + response.body());
        }
    }

    public <T> T sendPostRequest(String endpoint, String requestBody, Class<T> responseType) throws Exception {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBaseUrl() + endpoint))
            .POST(requestBody != null ? HttpRequest.BodyPublishers.ofString(requestBody) : HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "application/json")
            .build();
        //@formatter:on
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() >= 200 && response.statusCode() < 300) {
            return responseType == Void.class ? null : OBJECT_MAPPER.readValue(response.body(), responseType);
        }
        else {
            throw new RuntimeException(
                    "POST request failed with status: " + response.statusCode() + " - " + response.body());
        }
    }

    protected <T> String convertToJSONString(T object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }
}
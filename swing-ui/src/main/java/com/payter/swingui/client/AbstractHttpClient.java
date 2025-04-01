// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public abstract class AbstractHttpClient {

    protected HttpClient client;
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public AbstractHttpClient() {
        this.client = HttpClient.newHttpClient();
    }

    protected abstract String getBaseUrl();

    protected <T> T sendPostRequest(String endpoint, Object requestBodyDetails, Class<T> responseType)
            throws Exception {
        String requestBody;
        if(requestBodyDetails instanceof String) {
            requestBody = (String) requestBodyDetails;
        }
        else {
            requestBody = OBJECT_MAPPER.writeValueAsString(requestBodyDetails);
        }

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + endpoint))
                .header("Content-Type", "application/json").POST(BodyPublishers.ofString(requestBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200 || response.statusCode() == 201) {
            if(responseType != Void.class) {
                return OBJECT_MAPPER.readValue(response.body(), responseType);
            }
            return null;
        }
        else {
            if(response.body() == null) {
                throw new RuntimeException("Failed to send POST request. HTTP status: " + response.statusCode());
            }
            else {
                throw new RuntimeException(
                        "Failed to send POST request. HTTP status: " + response.statusCode() + " - " + response.body());
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T sendGetRequest(String endpoint, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + endpoint)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200) {
            if(responseType != Void.class && response.body() != null && !response.body().isEmpty()) {
                try {
                    return OBJECT_MAPPER.readValue(response.body(), responseType);
                }
                catch(Exception e) {
                    return (T) response.body();
                }
            }
            return null;
        }
        else {
            throw new RuntimeException("Failed to send GET request. HTTP status: " + response.statusCode());
        }
    }
}
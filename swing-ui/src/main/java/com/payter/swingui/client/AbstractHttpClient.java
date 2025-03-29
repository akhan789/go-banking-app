// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class AbstractHttpClient {

    protected static final String BASE_URL = "http://localhost:8000";
    protected HttpClient client;
    protected ObjectMapper objectMapper;

    public AbstractHttpClient() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    protected <T> T sendPostRequest(String endpoint, Object requestBody, Class<T> responseType) throws Exception {
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json").POST(BodyPublishers.ofString(jsonRequestBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200 || response.statusCode() == 201) {
            if(responseType != Void.class) {
                return objectMapper.readValue(response.body(), responseType);
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

    protected <T> T sendGetRequest(String endpoint, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), responseType);
        }
        else {
            throw new RuntimeException("Failed to send GET request. HTTP status: " + response.statusCode());
        }
    }
}
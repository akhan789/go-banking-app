// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class HttpClientService {

    private static final Logger LOG = LogManager.getLogger(HttpClientService.class);
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public HttpClientService() {
    }

    public String get(Map<String, String> headers, String url) throws Exception {
        Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.uri(URI.create(url));
        requestBuilder.GET();
        if(headers != null) {
            headers.keySet().stream().forEach(key -> {
                requestBuilder.header(key, headers.get(key));
            });
        }
        HttpRequest request = requestBuilder.build();
        LOG.info("Sending request: " + request);
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public String post(Map<String, String> headers, String url, String body) throws Exception {
        Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.uri(URI.create(url));
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
        if(headers != null) {
            headers.keySet().stream().forEach(key -> {
                requestBuilder.header(key, headers.get(key));
            });
        }
        HttpRequest request = requestBuilder.build();
        LOG.info("Sending request: " + request + ": " + body);
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public void postAsync(Map<String, String> headers, String url, String body) {
        Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.uri(URI.create(url));
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
        if(headers != null) {
            headers.keySet().stream().forEach(key -> {
                requestBuilder.header(key, headers.get(key));
            });
        }
        HttpRequest request = requestBuilder.build();
        LOG.info("Sending request: " + request + ": " + body);
        HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public String put(Map<String, String> headers, String url, String body) throws Exception {
        Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.uri(URI.create(url));
        requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body));
        if(headers != null) {
            headers.keySet().stream().forEach(key -> {
                requestBuilder.header(key, headers.get(key));
            });
        }
        HttpRequest request = requestBuilder.build();
        LOG.info("Sending request: " + request + ": " + body);
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        if(status == 204) {
            // No content is allowed in response body
            exchange.sendResponseHeaders(status, -1);
            return;
        }
        String requestURL = exchange.getRequestURI().toString();
        byte[] bytes = (response != null) ? response.getBytes(StandardCharsets.UTF_8) : new byte[0];
        if(bytes.length != 0) {
            String responseBody = new String(bytes, StandardCharsets.UTF_8);
            LOG.info("Sending response for request: " + requestURL + ": " + responseBody);
            exchange.sendResponseHeaders(status, bytes.length);
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        else {
            LOG.info("Sending response for request: " + requestURL);
            exchange.sendResponseHeaders(status, -1);
        }
    }
}
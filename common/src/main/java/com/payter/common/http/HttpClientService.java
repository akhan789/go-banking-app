// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class HttpClientService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public HttpClientService() {
    }

    public String post(String url, String body) throws Exception {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        //@formatter:on
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public void postAsync(String url, String body) {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        //@formatter:on
        HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public String get(String url) throws Exception {
        //@formatter:off
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        //@formatter:on
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
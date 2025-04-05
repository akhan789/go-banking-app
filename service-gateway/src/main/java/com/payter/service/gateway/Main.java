// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.gateway;

import java.net.InetSocketAddress;

import com.payter.common.auth.SimpleAuthenticator;
import com.payter.common.http.HttpClientService;
import com.payter.common.util.ConfigUtil;
import com.payter.service.gateway.controller.GatewayController;
import com.payter.service.gateway.service.DefaultGatewayService;
import com.payter.service.gateway.service.GatewayService;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class Main {

    private static final int PORT = Integer.valueOf(ConfigUtil.loadProperty("gateway.port", "8000"));

    public static void main(String[] args) throws Exception {
        HttpClientService httpClientService = new HttpClientService();
        GatewayService service = new DefaultGatewayService(new SimpleAuthenticator(), httpClientService);
        GatewayController controller = new GatewayController(service);
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", controller::handle);
        server.start();
        System.out.println("Service Gateway running on port " + PORT + "...");
    }
}
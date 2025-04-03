package com.payter.service.gateway;

import com.payter.common.http.HttpClientService;

public class Main {
    public static void main(String[] args) throws Exception {
        // Dependencies
        HttpClientService httpClient = new HttpClientService();
        //        SimpleAuthenticator authenticator = new SimpleAuthenticator();
        //        GatewayServiceImpl service = new GatewayServiceImpl(httpClient, authenticator);
        //        GatewayController controller = new GatewayController(service);
        //
        //        // Server setup
        //        HttpServer server = HttpServer.create(new InetSocketAddress(8084), 0);
        //        server.createContext("/", controller::handle);
        //        server.start();
        //        System.out.println("Service Gateway running on port 8084...");
    }
}
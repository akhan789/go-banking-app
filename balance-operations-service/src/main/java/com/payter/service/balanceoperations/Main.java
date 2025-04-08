// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations;

import java.net.InetSocketAddress;

import com.payter.common.auth.SimpleAuthenticator;
import com.payter.common.http.HttpClientService;
import com.payter.common.util.ConfigUtil;
import com.payter.common.util.Util;
import com.payter.service.balanceoperations.controller.BalanceOperationsController;
import com.payter.service.balanceoperations.repository.BalanceOperationsRepository;
import com.payter.service.balanceoperations.repository.SQLiteBalanceOperationsRepository;
import com.payter.service.balanceoperations.service.BalanceOperationsService;
import com.payter.service.balanceoperations.service.DefaultBalanceOperationsService;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Util.createDbDirectoryIfNotExists();
        String dbUrl = ConfigUtil.loadProperty("balanceoperations.db.connection.url",
                "jdbc:sqlite:db/balanceoperations.db");
        HttpClientService httpClientService = new HttpClientService();
        BalanceOperationsRepository repository = new SQLiteBalanceOperationsRepository(dbUrl);
        BalanceOperationsService service = new DefaultBalanceOperationsService(repository, httpClientService);
        BalanceOperationsController controller = new BalanceOperationsController(new SimpleAuthenticator(), service);
        int port = Integer.valueOf(ConfigUtil.loadProperty("balanceoperations.port", "8002"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(ConfigUtil.loadProperty("balanceoperations.endpoint", "/balanceoperations"),
                controller::handle);
        server.start();
        System.out.println("Balance Operations Service running on port " + port + "...");
    }
}
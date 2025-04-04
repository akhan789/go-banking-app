// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

import com.payter.common.http.HttpClientService;
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
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:db/balanceoperations.db")) {
            HttpClientService httpClientService = new HttpClientService();
            BalanceOperationsRepository repository = new SQLiteBalanceOperationsRepository(conn);
            BalanceOperationsService service = new DefaultBalanceOperationsService(repository, httpClientService);
            BalanceOperationsController controller = new BalanceOperationsController(service);
            HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);
            server.createContext("/balanceoperations", controller::handle);
            server.start();
            System.out.println("Balance Operations Service running on port 8002...");
        }
    }
}
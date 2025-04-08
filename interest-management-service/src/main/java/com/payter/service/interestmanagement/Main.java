// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement;

import java.net.InetSocketAddress;

import com.payter.common.auth.SimpleAuthenticator;
import com.payter.common.http.HttpClientService;
import com.payter.common.util.ConfigUtil;
import com.payter.common.util.Util;
import com.payter.service.interestmanagement.controller.InterestManagementController;
import com.payter.service.interestmanagement.repository.InterestManagementRepository;
import com.payter.service.interestmanagement.repository.SQLiteInterestManagementRepository;
import com.payter.service.interestmanagement.service.DefaultInterestManagementService;
import com.payter.service.interestmanagement.service.InterestManagementService;
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
        String dbUrl = ConfigUtil.loadProperty("interestmanagement.db.connection.url",
                "jdbc:sqlite:db/interestmanagement.db");
        HttpClientService httpClientService = new HttpClientService();
        InterestManagementRepository repository = new SQLiteInterestManagementRepository(dbUrl);
        InterestManagementService service = new DefaultInterestManagementService(repository, httpClientService);
        InterestManagementController controller = new InterestManagementController(new SimpleAuthenticator(), service);
        int port = Integer.valueOf(ConfigUtil.loadProperty("interestmanagement.port", "8003"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(ConfigUtil.loadProperty("interestmanagement.endpoint", "/interestmanagement"),
                controller::handle);
        server.start();
        service.startInterestApplication();
        System.out.println("Interest Management Service running on port " + port + "...");
        Thread.currentThread().join();
    }
}
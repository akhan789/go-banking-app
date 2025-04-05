// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

import com.payter.common.auth.SimpleAuthenticator;
import com.payter.common.http.HttpClientService;
import com.payter.common.util.ConfigUtil;
import com.payter.common.util.Util;
import com.payter.service.accountmanagement.controller.AccountManagementController;
import com.payter.service.accountmanagement.repository.AccountManagementRepository;
import com.payter.service.accountmanagement.repository.SQLiteAccountManagementRepository;
import com.payter.service.accountmanagement.service.AccountManagementService;
import com.payter.service.accountmanagement.service.DefaultAccountManagementService;
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
        try(Connection conn = DriverManager.getConnection(ConfigUtil.loadProperty("accountmanagement.db.connection.url",
                "jdbc:sqlite:db/accountmanagement.db"))) {
            HttpClientService httpClientService = new HttpClientService();
            AccountManagementRepository repository = new SQLiteAccountManagementRepository(conn);
            AccountManagementService service = new DefaultAccountManagementService(repository, httpClientService);
            AccountManagementController controller = new AccountManagementController(new SimpleAuthenticator(),
                    service);
            int port = Integer.valueOf(ConfigUtil.loadProperty("accountmanagement.port", "8001"));
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(ConfigUtil.loadProperty("accountManagement.endpoint", "/accountmanagement"),
                    controller::handle);
            server.start();
            System.out.println("Account Management Service running on port " + port + "...");
        }
        catch(Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
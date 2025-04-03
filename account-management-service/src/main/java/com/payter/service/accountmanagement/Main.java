// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
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
        Parser parser = ParserFactory.getParser(ParserType.JSON);
        Util.createDbDirectoryIfNotExists();
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:db/account.db")) {
            AccountManagementRepository repository = new SQLiteAccountManagementRepository(conn);
            AccountManagementService service = new DefaultAccountManagementService(repository);
            AccountManagementController controller = new AccountManagementController(service, parser);
            HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
            server.createContext("/accounts", controller::handle);
            server.start();
            System.out.println("Account Management Service running on port 8001...");
        }
    }
}
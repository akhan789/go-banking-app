// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging;

import java.net.InetSocketAddress;

import com.payter.common.auth.SimpleAuthenticator;
import com.payter.common.util.ConfigUtil;
import com.payter.common.util.Util;
import com.payter.service.auditlogging.controller.AuditLoggingController;
import com.payter.service.auditlogging.repository.AuditLoggingRepository;
import com.payter.service.auditlogging.repository.SQLiteAuditLoggingRepository;
import com.payter.service.auditlogging.service.AuditLoggingService;
import com.payter.service.auditlogging.service.DefaultAuditLoggingService;
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
        String dbUrl = ConfigUtil.loadProperty("auditlogging.db.connection.url", "jdbc:sqlite:db/auditlogging.db");
        AuditLoggingRepository repository = new SQLiteAuditLoggingRepository(dbUrl);
        AuditLoggingService service = new DefaultAuditLoggingService(repository);
        AuditLoggingController controller = new AuditLoggingController(new SimpleAuthenticator(), service);
        int port = Integer.valueOf(ConfigUtil.loadProperty("auditlogging.port", "8004"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(ConfigUtil.loadProperty("auditlogging.endpoint", "/auditlogging"), controller::handle);
        server.start();
        System.out.println("Audit Logging Service running on port " + port + "...");
    }
}
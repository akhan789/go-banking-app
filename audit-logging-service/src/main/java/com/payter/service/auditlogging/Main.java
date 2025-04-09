// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

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
@SuppressWarnings("resource")
public class Main {

    static {
        ConfigurationBuilder<BuiltConfiguration> configurationBuilder = ConfigurationBuilderFactory
                .newConfigurationBuilder();

        // Layout Builder.
        LayoutComponentBuilder layoutComponentBuilder = configurationBuilder.newLayout("PatternLayout");
        layoutComponentBuilder.addAttribute("pattern", "%d{ISO8601} [%t] %p %c{3} - %m%n");

        // Console Appender.
        AppenderComponentBuilder consoleAppender = configurationBuilder.newAppender("stdout", "Console");
        consoleAppender.add(layoutComponentBuilder);
        consoleAppender.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        configurationBuilder.add(consoleAppender);

        // File Appender.
        AppenderComponentBuilder rollingFileAppender = configurationBuilder.newAppender("rolling", "RollingFile");
        rollingFileAppender.add(layoutComponentBuilder);
        //@formatter:off
        ComponentBuilder<?> triggeringPolicies = configurationBuilder.newComponent("Policies")
            .addComponent(configurationBuilder.newComponent("TimeBasedTriggeringPolicy"))
            .addComponent(configurationBuilder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "512 MB"));
        //@formatter:on
        rollingFileAppender.addComponent(triggeringPolicies);
        rollingFileAppender.addAttribute("filePattern", "logs/service-%d{MM-dd-yy}.log");
        rollingFileAppender.addAttribute("fileName", "logs/service.log");
        configurationBuilder.add(rollingFileAppender);

        // Root Logger.
        RootLoggerComponentBuilder rootLogger = configurationBuilder.newRootLogger(Level.ALL);
        rootLogger.add(configurationBuilder.newAppenderRef("stdout"));
        rootLogger.add(configurationBuilder.newAppenderRef("rolling"));
        configurationBuilder.add(rootLogger);

        // Initialise.
        Configurator.initialize(configurationBuilder.build());
    }

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
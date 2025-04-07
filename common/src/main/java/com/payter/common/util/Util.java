// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.common.util;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.payter.common.dto.auditlogging.AuditLoggingRequestDTO;
import com.payter.common.dto.auditlogging.AuditLoggingRequestDTO.EventType;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;

/**
 * 
 * 
 * @author AK
 * @since 0.0.1_SNAPSHOT
 * @created 3 Apr 2025
 * @version $Revision$
 */
public final class Util {

    private Util() {
    }

    public static void createDbDirectoryIfNotExists() {
        String dbDirectory = ConfigUtil.loadProperty("util.db.directory", "db");
        File directory = new File(dbDirectory);
        if(!directory.exists()) {
            directory.mkdirs();
            System.out.println("Database directory created: " + dbDirectory);
        }
    }

    public static void logAudit(final HttpClientService httpClientService, final EventType eventType,
            final String details) {
        int retries = 3;
        while(retries > 0) {
            try {
                Parser parser = ParserFactory.getParser(ParserType.JSON);
                Map<String, String> headers = new HashMap<>();
                headers.put("X-API-Key", "internal");
                httpClientService.postAsync(headers,
                        ConfigUtil.loadProperty("util.auditlogging.endpoint", "http://localhost:8004/auditlogging"),
                        parser.serialise(new AuditLoggingRequestDTO(eventType, details)));
                return;
            }
            catch(Exception e) {
                retries--;
                if(retries == 0) {
                    System.err.println("Audit logging failed after retries: " + e.getMessage());
                }
            }
        }
    }

    public static String getQueryParam(String query, String paramName) {
        if(query == null || paramName == null) {
            return null;
        }
        for(String param : query.split("&")) {
            String[] pair = param.split("=");
            if(pair.length == 2 && pair[0].equals(paramName)) {
                return pair[1];
            }
        }
        return null;
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
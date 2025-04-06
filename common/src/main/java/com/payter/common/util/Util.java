// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.common.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.payter.common.http.HttpClientService;

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

    public static void logAudit(final HttpClientService httpClientService, final String message) {
        int retries = 3;
        while(retries > 0) {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-API-Key", "internal");
                httpClientService.postAsync(headers,
                        ConfigUtil.loadProperty("util.auditlogging.endpoint", "http://localhost:8004/auditlogging"),
                        message);
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
}
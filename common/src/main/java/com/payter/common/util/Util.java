// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.common.util;

import java.io.File;

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
        // TODO: configurable.
        String dbDirectory = "db";
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
                httpClientService.postAsync("http://localhost:8003/audit", message);
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
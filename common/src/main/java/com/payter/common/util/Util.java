// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.

package com.payter.common.util;

import java.io.File;

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
}
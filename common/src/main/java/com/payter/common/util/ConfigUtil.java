// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class ConfigUtil {

    private ConfigUtil() {
    }

    public static boolean saveProperty(String propertyName, String propertyValue) {
        if(propertyName == null || propertyValue == null) {
            return false;
        }
        try {
            File propertiesFile = new File("config/service.properties");
            Properties properties = loadPropertiesFile(propertiesFile);
            try(OutputStream fileOutputStream = new FileOutputStream(propertiesFile)) {
                properties.setProperty(propertyName, propertyValue);
                properties.store(fileOutputStream, null);
            }
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

    public static String loadProperty(String propertyName, String defaultValue) {
        if(propertyName == null || propertyName.length() == 0) {
            return null;
        }
        try {
            Properties properties = loadPropertiesFile(new File("config/service.properties"));
            String propertyValue = null;
            if(properties != null && (propertyValue = properties.getProperty(propertyName)) != null) {
                return propertyValue;
            }
            return defaultValue;
        }
        catch(IOException e) {
            return null;
        }
    }

    private static Properties loadPropertiesFile(File propertiesFile) throws FileNotFoundException, IOException {
        if(!propertiesFile.exists()) {
            boolean created = false;
            File parentFile = propertiesFile.getParentFile();
            if(!parentFile.exists()) {
                if(created = parentFile.mkdirs()) {
                    created = propertiesFile.createNewFile();
                }
            }
            else {
                created = propertiesFile.createNewFile();
            }
            if(!created) {
                return null;
            }
        }
        Properties properties = new Properties();
        try(InputStream fileInputStream = new FileInputStream(propertiesFile)) {
            properties.load(fileInputStream);
            return properties;
        }
    }
}
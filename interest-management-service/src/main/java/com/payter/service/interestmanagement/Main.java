// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement;

import java.sql.Connection;
import java.sql.DriverManager;

import com.payter.common.http.HttpClientService;
import com.payter.common.util.Util;
import com.payter.service.interestmanagement.repository.InterestManagementRepository;
import com.payter.service.interestmanagement.repository.SQLiteInterestManagementRepository;
import com.payter.service.interestmanagement.service.DefaultInterestManagementService;
import com.payter.service.interestmanagement.service.InterestManagementService;

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
        HttpClientService httpClientService = new HttpClientService();
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:db/interestmanagement.db")) {
            InterestManagementRepository repository = new SQLiteInterestManagementRepository(conn);
            InterestManagementService service = new DefaultInterestManagementService(repository, httpClientService);
            service.startInterestApplication();
            System.out.println("Interest Service running...");
            Thread.currentThread().join();
        }
    }
}
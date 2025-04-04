// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.entity.Account.Status;
import com.payter.service.balanceoperations.entity.BalanceOperation;
import com.payter.service.interestmanagement.entity.InterestManagement;
import com.payter.service.interestmanagement.repository.InterestManagementRepository;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class DefaultInterestManagementService implements InterestManagementService {

    private final InterestManagementRepository repository;
    private final HttpClientService httpClientService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public DefaultInterestManagementService(InterestManagementRepository repository,
            HttpClientService httpClientService) {
        this.repository = repository;
        this.httpClientService = httpClientService;
    }

    @Override
    public void startInterestApplication() {
        scheduler.scheduleAtFixedRate(this::applyInterest, 0, 24, TimeUnit.HOURS);
    }

    private void applyInterest() {
        try {
            Parser parser = ParserFactory.getParser(ParserType.JSON);
            InterestManagement config = repository.findLatest();
            String response = httpClientService.get("http://localhost:8001/accountmanagement");
            List<Account> accounts = parser.deserialiseList(response, Account.class);
            for(Account account : accounts) {
                if(account.getStatus() == Status.ACTIVE) {
                    BalanceOperation balanceOperation = new BalanceOperation();
                    balanceOperation.setAccountId(account.getAccountId());
                    balanceOperation.setAmount(account.getBalance().multiply(config.getDailyRate()));
                    String json = parser.serialise(balanceOperation);
                    httpClientService.post("http://localhost:8002/balanceoperations/credit", json);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
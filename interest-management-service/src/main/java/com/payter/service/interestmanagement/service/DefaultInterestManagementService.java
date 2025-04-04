// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.Util;
import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.entity.Account.Status;
import com.payter.service.balanceoperations.entity.BalanceOperation;
import com.payter.service.interestmanagement.entity.InterestManagement;
import com.payter.service.interestmanagement.entity.InterestManagement.CalculationFrequency;
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
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private long currentPeriod = 24; // Default to daily (hours)

    public DefaultInterestManagementService(InterestManagementRepository repository,
            HttpClientService httpClientService) {
        this.repository = repository;
        this.httpClientService = httpClientService;
    }

    @Override
    public void startInterestApplication() {
        scheduler.scheduleAtFixedRate(this::applyInterest, 0, currentPeriod, TimeUnit.HOURS);
    }

    @Override
    public InterestManagement configureInterest(BigDecimal dailyRate, CalculationFrequency calculationFrequency)
            throws Exception {
        if(dailyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Daily rate cannot be negative");
        }

        InterestManagement interestManagement = new InterestManagement();
        interestManagement.setDailyRate(dailyRate);
        interestManagement.setCalculationFrequency(calculationFrequency);
        InterestManagement savedConfig = repository.save(interestManagement);

        // Adjust scheduling based on frequency
        scheduler.shutdownNow(); // Stop current schedule
        scheduler.awaitTermination(5, TimeUnit.SECONDS);
        currentPeriod = switch(calculationFrequency) {
            case DAILY -> 24;        // 24 hours
            case WEEKLY -> 168;      // 7 days * 24 hours
            case MONTHLY -> 720;     // 30 days * 24 hours
            default -> throw new IllegalStateException("Unexpected frequency");
        };
        ScheduledExecutorService newScheduler = Executors.newSingleThreadScheduledExecutor();
        newScheduler.scheduleAtFixedRate(this::applyInterest, 0, currentPeriod, TimeUnit.HOURS);
        this.scheduler = newScheduler;

        Util.logAudit(httpClientService, "Interest management updated: dailyRate=" + dailyRate
                + ", calculationFrequency=" + calculationFrequency);
        return savedConfig;
    }

    @Override
    public InterestManagement getLatestInterestManagement() throws Exception {
        return repository.findLatest();
    }

    private void applyInterest() {
        try {
            InterestManagement interestManagement = repository.findLatest();
            String response = httpClientService.get("http://localhost:8001/accountmanagement");
            Parser parser = ParserFactory.getParser(ParserType.JSON);
            List<Account> accounts = parser.deserialiseList(response, Account.class);
            for(Account account : accounts) {
                if(account.getStatus() == Status.ACTIVE) {
                    BigDecimal interest = calculateInterest(account.getBalance(), interestManagement.getDailyRate(),
                            interestManagement.getCalculationFrequency());
                    if(interest.compareTo(BigDecimal.ZERO) > 0) {
                        BalanceOperation balanceOperation = new BalanceOperation();
                        balanceOperation.setAccountId(account.getAccountId());
                        balanceOperation.setAmount(interest);
                        String message = parser.serialise(balanceOperation);
                        httpClientService.post("http://localhost:8002/balanceoperations/credit", message);
                    }
                }
            }
            Util.logAudit(httpClientService, "Interest applied to active accounts");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private BigDecimal calculateInterest(BigDecimal balance, BigDecimal dailyRate,
            CalculationFrequency calculationFrequency) {
        int daysPerPeriod = switch(calculationFrequency) {
            case DAILY -> 1;
            case WEEKLY -> 7;
            case MONTHLY -> 30;
            default -> throw new IllegalStateException("Unexpected frequency");
        };
        return balance.multiply(dailyRate).multiply(BigDecimal.valueOf(daysPerPeriod));
    }
}
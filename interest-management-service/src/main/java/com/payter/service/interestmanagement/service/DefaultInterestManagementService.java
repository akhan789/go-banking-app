// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.payter.common.http.HttpClientService;
import com.payter.common.parser.Parser;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.ConfigUtil;
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
        scheduler.scheduleAtFixedRate(this::applyScheduledInterest, 0, currentPeriod, TimeUnit.HOURS);
    }

    @Override
    public InterestManagement configureInterest(BigDecimal dailyRate, CalculationFrequency calculationFrequency)
            throws Exception {
        InterestManagement latest = getLatestInterestManagement();
        InterestManagement interestManagement = latest != null ? latest : new InterestManagement();
        if(dailyRate != null) {
            if(dailyRate.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Daily rate cannot be negative");
            }
            interestManagement.setDailyRate(dailyRate);
        }
        if(calculationFrequency != null) {
            interestManagement.setCalculationFrequency(calculationFrequency);
        }
        InterestManagement savedConfig = repository.save(interestManagement);
        // Adjust scheduling only if frequency changes
        if(calculationFrequency != null) {
            scheduler.shutdownNow();
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
            currentPeriod = switch(calculationFrequency) {
                case DAILY -> 24;   // 24 hours
                case WEEKLY -> 168; // 7 days * 24 hours
                case MONTHLY -> 720;// 30 days * 24 hours
                default -> throw new IllegalStateException("Unexpected frequency");
            };
            ScheduledExecutorService newScheduler = Executors.newSingleThreadScheduledExecutor();
            newScheduler.scheduleAtFixedRate(this::applyScheduledInterest, 0, currentPeriod, TimeUnit.HOURS);
            this.scheduler = newScheduler;
        }
        Util.logAudit(httpClientService,
                "Interest management updated: " + (dailyRate != null ? "dailyRate=" + dailyRate : "")
                        + (dailyRate != null && calculationFrequency != null ? ", " : "")
                        + (calculationFrequency != null ? "calculationFrequency=" + calculationFrequency : ""));
        return savedConfig;
    }

    private void applyScheduledInterest() {
        try {
            InterestManagement interestManagement = getLatestInterestManagement();
            if(interestManagement == null || interestManagement.getDailyRate().compareTo(BigDecimal.ZERO) == 0) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            if(interestManagement.getNextApplicationAt() != null
                    && now.isAfter(interestManagement.getNextApplicationAt())) {
                applyInterestToAccounts(interestManagement, false);
                interestManagement.setLastAppliedAt(now);
                interestManagement.setNextApplicationAt(
                        calculateNextApplicationTime(interestManagement.getCalculationFrequency()));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Util.logAudit(httpClientService, "Failed to apply interest: " + e.getMessage());
        }
    }

    @Override
    public InterestManagement getLatestInterestManagement() throws Exception {
        InterestManagement latest = repository.findLatest();
        if(latest == null) {
            throw new IllegalStateException("No interest management configuration found");
        }
        return latest;
    }

    @Override
    public void applyInterest(boolean force) {
        try {
            InterestManagement interestManagement = getLatestInterestManagement();
            applyInterestToAccounts(interestManagement, force);
            interestManagement.setLastAppliedAt(LocalDateTime.now());
            interestManagement
                    .setNextApplicationAt(calculateNextApplicationTime(interestManagement.getCalculationFrequency()));
            repository.save(interestManagement);
        }
        catch(Exception e) {
            e.printStackTrace();
            Util.logAudit(httpClientService, "Failed to apply interest: " + e.getMessage());
        }
    }

    private void applyInterestToAccounts(InterestManagement interestManagement, boolean force) throws Exception {
        final Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", "internal");
        String response = httpClientService.get(headers,
                ConfigUtil.loadProperty("service.accountmanagement.url", "http://localhost:8001")
                        + ConfigUtil.loadProperty("service.accountmanagement.endpoint", "/accountmanagement"));
        Parser parser = ParserFactory.getParser(ParserType.JSON);
        List<Account> accounts;
        if(parser.isList(response)) {
            accounts = parser.deserialiseList(response, Account.class);
        }
        else {
            Account account = parser.deserialise(response, Account.class);
            accounts = List.of(account);
        }

        // Calculate the interest based on a global daily rate
        // and credit to each qualifying account. So if no days
        // have passed there is no interest applied to the accounts
        // unless force apply was passed in which case immediately apply
        // a flat rate based on globalDailyRate.
        BigDecimal dailyRate = interestManagement.getDailyRate();
        int days = (int) ChronoUnit.DAYS.between(interestManagement.getLastAppliedAt(), LocalDateTime.now());
        for(Account account : accounts) {
            if(account.getStatus() == Status.ACTIVE) {
                BigDecimal interest = force ? calculateInterestForced(account.getBalance(), dailyRate)
                        : calculateInterest(account.getBalance(), dailyRate, days);
                if(interest.compareTo(BigDecimal.ZERO) > 0) {
                    BalanceOperation balanceOperation = new BalanceOperation();
                    balanceOperation.setAccountId(account.getAccountId());
                    balanceOperation.setAmount(interest);
                    String message = parser.serialise(balanceOperation);
                    httpClientService.post(headers,
                            ConfigUtil.loadProperty("service.balanceoperations.url", "http://localhost:8002")
                                    + ConfigUtil.loadProperty("service.balanceoperations.endpoint",
                                            "/balanceoperations")
                                    + ConfigUtil.loadProperty("service.balanceoperations.credit.endpoint", "/credit"),
                            message);
                }
                Util.logAudit(httpClientService, "Credited " + interest + " to Account " + account.getAccountId());
            }
        }
    }

    private BigDecimal calculateInterest(BigDecimal balance, BigDecimal dailyRate, int days) {
        return balance.multiply(dailyRate.divide(BigDecimal.valueOf(100))).multiply(BigDecimal.valueOf(days));
    }

    private BigDecimal calculateInterestForced(BigDecimal balance, BigDecimal dailyRate) {
        return balance.multiply(dailyRate.divide(BigDecimal.valueOf(100)));
    }

    private LocalDateTime calculateNextApplicationTime(CalculationFrequency calculationFrequency) {
        LocalDateTime now = LocalDateTime.now();
        switch(calculationFrequency) {
            case DAILY:
                return now.plusDays(1);
            case WEEKLY:
                return now.plusWeeks(1);
            case MONTHLY:
                return now.plusMonths(1);
            default:
                return now.plusMonths(1);
        }
    }

    @Override
    public void skipTime(int periodsToSkip) throws Exception {
        InterestManagement interestManagement = getLatestInterestManagement();
        // Simulate time skip by adjusting timestamps
        LocalDateTime now = LocalDateTime.now();
        // Set last application before skip
        interestManagement.setLastAppliedAt(now.minusDays(periodsToSkip));
        // Set next application in the past
        interestManagement.setNextApplicationAt(now.minusDays(1));
        repository.save(interestManagement);
        applyScheduledInterest();
    }
}
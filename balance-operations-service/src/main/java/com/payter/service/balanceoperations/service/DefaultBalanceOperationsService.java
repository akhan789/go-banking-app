// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.payter.common.dto.accountmanagement.AccountDTO;
import com.payter.common.http.HttpClientService;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.ConfigUtil;
import com.payter.common.util.Util;
import com.payter.service.balanceoperations.entity.BalanceOperation;
import com.payter.service.balanceoperations.entity.BalanceOperation.Type;
import com.payter.service.balanceoperations.repository.BalanceOperationsRepository;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class DefaultBalanceOperationsService implements BalanceOperationsService {

    private static final ConcurrentHashMap<String, Lock> ACCOUNT_LOCKS = new ConcurrentHashMap<>();
    private static final String ACCOUNT_SERVICE_URL = ConfigUtil.loadProperty("service.accountManagement.url",
            "http://localhost:8001")
            + ConfigUtil.loadProperty("service.accountManagement.endpoint", "/accountmanagement");
    private static final String INTERNAL_API_KEY = "internal";

    private final BalanceOperationsRepository repository;
    private final HttpClientService httpClientService;

    public DefaultBalanceOperationsService(BalanceOperationsRepository repository,
            HttpClientService httpClientService) {
        this.repository = repository;
        this.httpClientService = httpClientService;
    }

    @Override
    public BigDecimal getBalance(String accountId) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", INTERNAL_API_KEY);
        String response = httpClientService.get(headers, ACCOUNT_SERVICE_URL + "/" + accountId);
        AccountDTO account = ParserFactory.getParser(ParserType.JSON).deserialise(response, AccountDTO.class);
        return account.getBalance();
    }

    @Override
    public BalanceOperation credit(BalanceOperation balanceOperation) throws Exception {
        Lock accountLock = getAccountLock(balanceOperation.getAccountId());
        accountLock.lock();
        try {
            validateAccountStatus(balanceOperation.getAccountId());
            balanceOperation.setType(Type.CREDIT);
            BalanceOperation saved = repository.save(balanceOperation);
            updateAccountBalance(balanceOperation.getAccountId(), balanceOperation.getAmount(), true);
            Util.logAudit(httpClientService, "Credit transaction: " + saved.getId());
            return saved;
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public BalanceOperation debit(BalanceOperation balanceOperation) throws Exception {
        Lock accountLock = getAccountLock(balanceOperation.getAccountId());
        accountLock.lock();
        try {
            validateAccountStatus(balanceOperation.getAccountId());
            BigDecimal balance = getBalance(balanceOperation.getAccountId());
            if(balance.compareTo(balanceOperation.getAmount()) < 0) {
                throw new IllegalStateException("Insufficient funds");
            }
            balanceOperation.setType(Type.DEBIT);
            BalanceOperation saved = repository.save(balanceOperation);
            updateAccountBalance(balanceOperation.getAccountId(), balanceOperation.getAmount(), false);
            Util.logAudit(httpClientService, "Debit transaction: " + saved.getId());
            return saved;
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public BalanceOperation transfer(String fromAccountId, String toAccountId, BigDecimal amount) throws Exception {
        Lock fromLock = getAccountLock(fromAccountId);
        Lock toLock = getAccountLock(toAccountId);
        // Lock in a consistent order to avoid deadlocks
        Lock firstLock = fromAccountId.compareTo(toAccountId) < 0 ? fromLock : toLock;
        Lock secondLock = fromAccountId.compareTo(toAccountId) < 0 ? toLock : fromLock;
        firstLock.lock();
        try {
            secondLock.lock();
            try {
                validateAccountStatus(fromAccountId);
                validateAccountStatus(toAccountId);
                BigDecimal fromBalance = getBalance(fromAccountId);
                if(fromBalance.compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient funds in source account");
                }

                BalanceOperation debitBalanceOperation = new BalanceOperation();
                debitBalanceOperation.setAccountId(fromAccountId);
                debitBalanceOperation.setToAccountId(toAccountId);
                debitBalanceOperation.setAmount(amount);
                debitBalanceOperation.setType(Type.TRANSFER);

                BalanceOperation creditBalanceOperation = new BalanceOperation();
                creditBalanceOperation.setAccountId(toAccountId);
                creditBalanceOperation.setToAccountId(fromAccountId);
                creditBalanceOperation.setAmount(amount);
                creditBalanceOperation.setType(Type.TRANSFER);

                repository.saveTransfer(debitBalanceOperation, creditBalanceOperation);
                updateAccountBalance(fromAccountId, amount, false);
                updateAccountBalance(toAccountId, amount, true);
                Util.logAudit(httpClientService,
                        "Transfer from " + fromAccountId + " to " + toAccountId + ": " + amount);
                return debitBalanceOperation;
            }
            finally {
                secondLock.unlock();
            }
        }
        finally {
            firstLock.unlock();
        }
    }

    private void validateAccountStatus(String accountId) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", INTERNAL_API_KEY);
        String response = httpClientService.get(headers, ACCOUNT_SERVICE_URL + "/" + accountId);
        AccountDTO account = ParserFactory.getParser(ParserType.JSON).deserialise(response, AccountDTO.class);
        if(!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException("Account " + accountId + " is not active");
        }
    }

    private void updateAccountBalance(String accountId, BigDecimal amount, boolean isCredit) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", INTERNAL_API_KEY);
        String operation = isCredit ? "credit" : "debit";
        String url = ACCOUNT_SERVICE_URL + "/" + accountId + "/" + operation;
        String body = "{\"amount\": \"" + amount.toString() + "\"}";
        httpClientService.put(headers, url, body);
    }

    private Lock getAccountLock(String accountId) {
        return ACCOUNT_LOCKS.computeIfAbsent(accountId, key -> new ReentrantLock());
    }
}
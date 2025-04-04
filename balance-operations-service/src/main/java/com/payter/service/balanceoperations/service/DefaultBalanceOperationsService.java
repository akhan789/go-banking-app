// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.payter.common.http.HttpClientService;
import com.payter.common.parser.ParserFactory;
import com.payter.common.parser.ParserFactory.ParserType;
import com.payter.common.util.Util;
import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.entity.Account.Status;
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

    private final BalanceOperationsRepository repository;
    private final HttpClientService httpClientService;

    public DefaultBalanceOperationsService(BalanceOperationsRepository repository,
            HttpClientService httpClientService) {
        this.repository = repository;
        this.httpClientService = httpClientService;
    }

    @Override
    public BigDecimal getBalance(String accountId) throws Exception {
        return repository.calculateBalance(accountId);
    }

    @Override
    public BalanceOperation processCredit(BalanceOperation balanceOperation) throws Exception {
        Lock accountLock = getAccountLock(balanceOperation.getAccountId());
        accountLock.lock();
        try {
            validateAccountStatus(balanceOperation.getAccountId());
            balanceOperation.setType(Type.CREDIT);
            BalanceOperation saved = repository.save(balanceOperation);
            Util.logAudit(httpClientService, "Credit transaction: " + saved.getId());
            return saved;
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public BalanceOperation processDebit(BalanceOperation balanceOperation) throws Exception {
        Lock accountLock = getAccountLock(balanceOperation.getAccountId());
        accountLock.lock();
        try {
            validateAccountStatus(balanceOperation.getAccountId());
            BigDecimal balance = getBalance(balanceOperation.getAccountId());
            if(balance.compareTo(balanceOperation.getAmount()) < 0) {
                throw new IllegalStateException("Insufficient funds");
            }
            BalanceOperation saved = repository.save(balanceOperation);
            saved.setType(Type.DEBIT);
            Util.logAudit(httpClientService, "Debit transaction: " + saved.getId());
            return saved;
        }
        finally {
            accountLock.unlock();
        }
    }

    private void validateAccountStatus(String accountId) throws Exception {
        String response = httpClientService.get("http://localhost:8001/accountmanagement/" + accountId);
        Account account = ParserFactory.getParser(ParserType.JSON).deserialise(response, Account.class);
        if(account.getStatus() != Status.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
    }

    private Lock getAccountLock(String accountId) {
        return ACCOUNT_LOCKS.computeIfAbsent(accountId, key -> new ReentrantLock());
    }
}
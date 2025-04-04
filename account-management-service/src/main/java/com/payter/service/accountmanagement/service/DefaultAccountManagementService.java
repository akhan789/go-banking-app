// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.payter.common.http.HttpClientService;
import com.payter.common.util.Util;
import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.entity.Account.Status;
import com.payter.service.accountmanagement.repository.AccountManagementRepository;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class DefaultAccountManagementService implements AccountManagementService {

    private static final ConcurrentHashMap<String, Lock> ACCOUNT_LOCKS = new ConcurrentHashMap<>();

    private final AccountManagementRepository repository;
    private final HttpClientService httpClientService;

    public DefaultAccountManagementService(AccountManagementRepository repository,
            HttpClientService httpClientService) {
        this.repository = repository;
        this.httpClientService = httpClientService;
    }

    @Override
    public Account createAccount(Account account) throws Exception {
        Account saved = repository.save(account);
        Util.logAudit(httpClientService, "Account created: " + saved.getId());
        return saved;
    }

    @Override
    public Account suspendAccount(String accountId) throws Exception {
        Lock accountLock = getAccountLock(accountId);
        accountLock.lock();
        try {
            repository.updateStatus(accountId, Status.SUSPENDED);
            Util.logAudit(httpClientService, "Account suspended: " + accountId);
            return repository.findByAccountId(accountId);
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public Account reactivateAccount(String accountId) throws Exception {
        Lock accountLock = getAccountLock(accountId);
        accountLock.lock();
        try {
            repository.updateStatus(accountId, Status.ACTIVE);
            Util.logAudit(httpClientService, "Account reactivated: " + accountId);
            return repository.findByAccountId(accountId);
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public void closeAccount(String accountId) throws Exception {
        Lock accountLock = getAccountLock(accountId);
        accountLock.lock();
        try {
            repository.updateStatus(accountId, Status.CLOSED);
            Util.logAudit(httpClientService, "Account closed: " + accountId);
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public Account getAccount(String accountId) throws Exception {
        Account account = repository.findByAccountId(accountId);
        if(account == null) {
            throw new RuntimeException("Account not found with ID: " + accountId);
        }
        return account;
    }

    private Lock getAccountLock(String accountId) {
        return ACCOUNT_LOCKS.computeIfAbsent(accountId, key -> new ReentrantLock());
    }
}
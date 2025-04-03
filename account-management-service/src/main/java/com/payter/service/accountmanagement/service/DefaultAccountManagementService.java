// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.security.auth.login.AccountNotFoundException;

import com.payter.common.http.HttpClientService;
import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.repository.AccountManagementRepository;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class DefaultAccountManagementService implements AccountManagementService {

    private static final HttpClientService HTTP_SERVICE = new HttpClientService();
    private final AccountManagementRepository repository;
    private final Lock accountLock = new ReentrantLock();

    public DefaultAccountManagementService(AccountManagementRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account createAccount(Account account) throws Exception {
        Account saved = repository.save(account);
        logAudit("Account created: " + saved.getId());
        return saved;
    }

    @Override
    public Account suspendAccount(long id) throws Exception {
        accountLock.lock();
        try {
            repository.updateStatus(id, "SUSPENDED");
            logAudit("Account suspended: " + id);
            return repository.findById(id);
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public Account reactivateAccount(long id) throws Exception {
        accountLock.lock();
        try {
            repository.updateStatus(id, "ACTIVE");
            logAudit("Account reactivated: " + id);
            return repository.findById(id);
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public void closeAccount(long id) throws Exception {
        accountLock.lock();
        try {
            repository.updateStatus(id, "CLOSED");
            logAudit("Account closed: " + id);
        }
        finally {
            accountLock.unlock();
        }
    }

    @Override
    public Account getAccount(long id) throws Exception {
        Account account = repository.findById(id);
        if(account == null) {
            throw new AccountNotFoundException("Account not found with ID: " + id);
        }
        return account;
    }

    private void logAudit(String message) {
        try {
            HTTP_SERVICE.postAsync("http://localhost:8003/audit", message);
        }
        catch(Exception e) {
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }
}
// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.repository;

import com.payter.service.accountmanagement.entity.Account;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface AccountManagementRepository {

    Account save(Account account) throws Exception;

    Account findById(long id) throws Exception;

    void updateStatus(long id, String status) throws Exception;
}
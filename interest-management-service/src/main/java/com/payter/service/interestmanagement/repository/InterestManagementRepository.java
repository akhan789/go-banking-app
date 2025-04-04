// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.repository;

import com.payter.service.interestmanagement.entity.InterestManagement;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface InterestManagementRepository {

    InterestManagement findLatest() throws Exception;
}
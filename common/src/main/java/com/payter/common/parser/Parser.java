// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.parser;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public interface Parser {

    public <T> String serialise(T entity) throws Exception;

    public <T> T deserialise(String message, Class<T> clazz) throws Exception;
}
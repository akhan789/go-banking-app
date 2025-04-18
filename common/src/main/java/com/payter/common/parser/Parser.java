// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.parser;

import java.util.List;
import java.util.Map;

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

    public <T> List<T> deserialiseList(String message, Class<T> elementType) throws Exception;

    public <K, V> Map<K, V> deserialiseMap(String message, Class<K> keyType, Class<V> valueType) throws Exception;

    public boolean isList(String entity) throws Exception;
}
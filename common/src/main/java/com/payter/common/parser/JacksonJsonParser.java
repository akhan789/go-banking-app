// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.parser;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
class JacksonJsonParser implements Parser {

    private final ObjectMapper objectMapper;

    public JacksonJsonParser() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String serialise(Object entity) throws Exception {
        return objectMapper.writeValueAsString(entity);
    }

    @Override
    public <T> T deserialise(String message, Class<T> clazz) throws Exception {
        return objectMapper.readValue(message, clazz);
    }

    @Override
    public <T> List<T> deserialiseList(String message, Class<T> elementType) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return objectMapper.readValue(message, type);
    }

    @Override
    public <K, V> Map<K, V> deserialiseMap(String message, Class<K> keyType, Class<V> valueType) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructMapType(Map.class, keyType, valueType);
        return objectMapper.readValue(message, type);
    }

    @Override
    public boolean isList(String input) throws Exception {
        JsonNode node = objectMapper.readTree(input);
        return node.isArray();
    }
}
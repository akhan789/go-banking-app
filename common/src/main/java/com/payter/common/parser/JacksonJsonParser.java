// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
}
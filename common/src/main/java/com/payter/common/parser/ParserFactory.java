// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.parser;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class ParserFactory {

    // TODO: make the parser configurable.
    private static final String IMPLEMENTATION_CLASS = "com.payter.common.parser.JacksonJsonParser";

    public enum ParserType {
        JSON
    }

    private ParserFactory() {
    }

    public static Parser getParser(ParserType parserType) {
        switch(parserType) {
            case JSON:
                try {
                    return (Parser) Class.forName(IMPLEMENTATION_CLASS).getDeclaredConstructor().newInstance();
                }
                catch(InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException
                        | ClassNotFoundException e) {
                    throw new IllegalStateException("Configuration error: Unable to load parser: " + e.getMessage());
                }
                // Potentially XML etc?
            default:
                throw new IllegalArgumentException("Parser type not supported: " + parserType);
        }
    }
}
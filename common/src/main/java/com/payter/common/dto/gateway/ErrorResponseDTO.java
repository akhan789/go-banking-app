// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.common.dto.gateway;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class ErrorResponseDTO {

    private String error;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
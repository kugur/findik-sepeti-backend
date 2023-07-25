package com.kolip.findiksepeti.common;

import org.springframework.util.StringUtils;

public class UpdateResponse<T> {
    String errorMessage;
    T updatedValue;

    public UpdateResponse() {
    }

    public UpdateResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public UpdateResponse(T updatedValue) {
        this.updatedValue = updatedValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getUpdatedValue() {
        return updatedValue;
    }

    public void setUpdatedValue(T updatedValue) {
        this.updatedValue = updatedValue;
    }

    public boolean isSuccessful() {
        return !StringUtils.hasText(errorMessage);
    }
}

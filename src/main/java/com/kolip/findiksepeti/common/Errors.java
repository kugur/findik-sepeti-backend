package com.kolip.findiksepeti.common;

public enum Errors {

    INVALID_ARGUMENT("invalid Argument"),
    UNKNOWN_ERROR("internal server error");

    public final String description;

    Errors(String description) {
        this.description = description;
    }




}

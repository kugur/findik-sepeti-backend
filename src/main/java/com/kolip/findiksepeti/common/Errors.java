package com.kolip.findiksepeti.common;

public enum Errors {

    INVALID_ARGUMENT("invalid Argument"),
    USERD_BY_PRODUCTS("used by products"),
    UNKNOWN_ERROR("internal server error");

    public final String description;

    Errors(String description) {
        this.description = description;
    }




}

package com.kolip.findiksepeti.user;

/**
 * Created by ugur.kolip on 19.02.2022
 */
public class InvalidArguments extends RuntimeException {
    public InvalidArguments(String message) {
        super(message);
    }
}

package com.kolip.findiksepeti.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.function.Function;

public enum LoginType {
    SOCIAL_LOGIN(CustomOAuth2AuthenticationToken.class,
            authentication -> ((CustomOAuth2AuthenticationToken) authentication).getUserInfo()),
    BASIC_AUTH(UsernamePasswordAuthenticationToken.class,
            authentication -> ((CustomUser) authentication.getPrincipal()));

    private final Class authenticationType;
    private final Function<Authentication, CustomUser> userInfo;

    LoginType(Class authenticationType, Function<Authentication, CustomUser> userInfo) {
        this.authenticationType = authenticationType;
        this.userInfo = userInfo;
    }

    public Class<?> getAuthenticationType() {
        return authenticationType;
    }

    public Function<Authentication, CustomUser> getUserInfo() {
        return userInfo;
    }
}

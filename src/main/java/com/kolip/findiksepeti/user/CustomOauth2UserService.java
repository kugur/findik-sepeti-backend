package com.kolip.findiksepeti.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CustomOauth2UserService {

    private UserService userService;

    public CustomOauth2UserService(UserService userService) {
        this.userService = userService;
    }

    public CustomUser getCurrentUser() {

        Object currentPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (currentPrincipal instanceof CustomOAuth2AuthenticationToken) {
            return ((CustomOAuth2AuthenticationToken) currentPrincipal).getUserInfo();
        }

        return null;
    }
}

package com.kolip.findiksepeti.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;

public class CustomOAuth2AuthenticationToken extends OAuth2AuthenticationToken {

    private CustomUser userInfo;

    /**
     * Constructs an {@code OAuth2AuthenticationToken} using the provided parameters.
     *
     * @param principal                      the user {@code Principal} registered with the OAuth 2.0 Provider
     * @param authorities                    the authorities granted to the user
     * @param authorizedClientRegistrationId the registration identifier of the
     *                                       {@link OAuth2AuthorizedClient Authorized Client}
     */
    public CustomOAuth2AuthenticationToken(OAuth2User principal, Collection<? extends GrantedAuthority> authorities, String authorizedClientRegistrationId) {
        this(principal, authorities, authorizedClientRegistrationId, null);
    }

    public CustomOAuth2AuthenticationToken(OAuth2User principal, Collection<? extends GrantedAuthority> authorities,
                                           String authorizedClientRegistrationId, CustomUser userInfo) {
        super(principal, authorities, authorizedClientRegistrationId);
        this.userInfo = userInfo;
    }

    public CustomUser getUserInfo() {
        return userInfo;
    }
}

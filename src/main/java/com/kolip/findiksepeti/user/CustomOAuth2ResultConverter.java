package com.kolip.findiksepeti.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomOAuth2ResultConverter implements Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> {
    private UserService userService;

    public CustomOAuth2ResultConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2AuthenticationToken convert(OAuth2LoginAuthenticationToken authenticationResult) {
        CustomUser user = null;
        Collection<GrantedAuthority> authorities = new ArrayList<>(authenticationResult.getAuthorities());
        String userEmail = getEmail(authenticationResult);

        if (StringUtils.hasText(userEmail)) {
            user = userService.getUser(userEmail);
        }

        if (user == null) {
            Collection<SimpleGrantedAuthority> userAuthorities = new ArrayList<>();
            userAuthorities.add(new SimpleGrantedAuthority(Roles.Pre_User.getRoleName()));
            user = new CustomUser("", "", "", userEmail, "", null, userAuthorities);
        }
        authorities.addAll(user.getAuthorities());


        return new CustomOAuth2AuthenticationToken(authenticationResult.getPrincipal(),
                authorities,
                authenticationResult.getClientRegistration().getRegistrationId(),
                user);
    }

    private String getEmail(OAuth2LoginAuthenticationToken authenticationResult) {
        return authenticationResult.getPrincipal().getAttribute("email");
    }
}

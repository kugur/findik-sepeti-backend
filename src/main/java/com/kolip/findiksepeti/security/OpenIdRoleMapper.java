package com.kolip.findiksepeti.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.*;

/**
 * Created by ugur.kolip on 30.12.2022.
 * RoleMapper class to use map roles gather from google sign in.
 */
public class OpenIdRoleMapper implements GrantedAuthoritiesMapper {

    private static String ADMIN_EMAIL = "asdugur.kolip@gmail.com";

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Collection<GrantedAuthority> mappedAuthorities = new HashSet<>();
        String email = "";

        for (GrantedAuthority grantedAuthority : authorities) {

            if (OidcUserAuthority.class.isInstance(grantedAuthority)) {
                email = ((OidcUserAuthority) grantedAuthority).getIdToken().getEmail();

            }
            mappedAuthorities.add(grantedAuthority);
        }

        // Added simple role
        SimpleGrantedAuthority addedRole =
                new SimpleGrantedAuthority(ADMIN_EMAIL.equals(email) ? "ROLE_ADMIN" : "ROLE_USER");
        mappedAuthorities.add(addedRole);

        return mappedAuthorities;
    }

    private boolean isOpenId(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(OidcUserAuthority.class::isInstance);
    }

}

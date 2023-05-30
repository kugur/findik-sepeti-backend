package com.kolip.findiksepeti.security;

import com.kolip.findiksepeti.user.CustomUser;
import com.kolip.findiksepeti.user.Roles;
import com.kolip.findiksepeti.user.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by ugur.kolip on 30.12.2022.
 * RoleMapper class to use map roles gather from google sign in.
 */
//@Service
public class OpenIdRoleMapper implements GrantedAuthoritiesMapper {

    private static String ADMIN_EMAIL = "asdugur.kolip@gmail.com";
    private UserService userService;

    public OpenIdRoleMapper(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Collection<GrantedAuthority> mappedAuthorities = new HashSet<>();
        String email = "";
        CustomUser user = null;

        for (GrantedAuthority grantedAuthority : authorities) {

            if (OidcUserAuthority.class.isInstance(grantedAuthority)) {
                email = ((OidcUserAuthority) grantedAuthority).getIdToken().getEmail();

            }
            mappedAuthorities.add(grantedAuthority);
        }

        // Added simple role
        user = userService.getUser(email);
        if (user == null) {
            mappedAuthorities.add(new SimpleGrantedAuthority(Roles.Pre_User.getRoleName()));
        } else {
            mappedAuthorities.addAll(user.getAuthorities());
        }
//        SimpleGrantedAuthority addedRole =
//                new SimpleGrantedAuthority(ADMIN_EMAIL.equals(email) ? "ROLE_ADMIN" : "ROLE_USER");
//        mappedAuthorities.add(addedRole);

        return mappedAuthorities;
    }

    private boolean isOpenId(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(OidcUserAuthority.class::isInstance);
    }

}

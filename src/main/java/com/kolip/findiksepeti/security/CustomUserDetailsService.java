package com.kolip.findiksepeti.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolip.findiksepeti.user.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        CustomUser user = new CustomUser("ugur", "kolip", "{noop}password", "ugur.kolip@gmail.com",
                Arrays.asList(authority));
        return new CustomUserDetails(user);
    }

    static final class CustomUserDetails extends CustomUser implements UserDetails {

        private static final List<GrantedAuthority> ROLE_USER = Collections
                .unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER"));

        CustomUserDetails(CustomUser customUser) {
            super(customUser.getFirstName(), customUser.getLastName(), customUser.getPassword(), customUser.getEmail(),
                    customUser.getAuthorities());
        }

        @JsonIgnore
        @Override
        public String getUsername() {
            return getEmail();
        }

        @JsonIgnore
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @JsonIgnore
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @JsonIgnore
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @JsonIgnore
        @Override
        public boolean isEnabled() {
            return true;
        }

    }
}

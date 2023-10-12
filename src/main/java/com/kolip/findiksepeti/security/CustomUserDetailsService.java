package com.kolip.findiksepeti.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolip.findiksepeti.user.CustomUser;
import com.kolip.findiksepeti.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        CustomUser user = userService.getUser(username);
//        CustomUser user = new CustomUser("ugur", "kolip", "{noop}password", "ugur.kolip@gmail.com",
//                Arrays.asList(authority));
        return new CustomUserDetails(user);
    }

    static final class CustomUserDetails extends CustomUser implements UserDetails {

//        private static final List<GrantedAuthority> ROLE_USER = Collections
//                .unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER"));

        CustomUserDetails(CustomUser customUser) {
            super(customUser.getFirstName(), customUser.getLastName(), customUser.getPassword(), customUser.getEmail(),
                    customUser.getAddress(), customUser.getGender(), customUser.getRoles());
            this.id = customUser.getId();
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

package com.kolip.findiksepeti.user;

import com.kolip.findiksepeti.converters.UserConverter;
import com.kolip.findiksepeti.exceptions.InvalidArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, UserConverter userConverter, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.userConverter = userConverter;
        this.roleRepository = roleRepository;
    }

    @Override
    public CustomUser getCurrentUser() {
        Authentication currentPrincipal = SecurityContextHolder.getContext().getAuthentication();

        if (currentPrincipal instanceof CustomOAuth2AuthenticationToken) {
            return ((CustomOAuth2AuthenticationToken) currentPrincipal).getUserInfo();
        } else if (currentPrincipal instanceof UsernamePasswordAuthenticationToken) {
            return (CustomUser) currentPrincipal.getPrincipal();
        }
        logger.warn("userInfo for current user is not expected type. {}", currentPrincipal.getClass());
        return null;
    }

    //TODO(ugur) burada iyilestirme yapilabilir . getCurrentUser ile ortak noktalar var !!!
    private void setAuthentication(CustomUser storedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof CustomOAuth2AuthenticationToken) {
            SecurityContextHolder.getContext().setAuthentication(new CustomOAuth2AuthenticationToken(
                    (OAuth2User) authentication.getPrincipal(),
                    storedUser.getAuthorities(),
                    ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(),
                    storedUser));
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(storedUser,
                    authentication.getCredentials(), authentication.getAuthorities()));
        }

    }

    @Override
    public CustomUser createUser(UserDto userDto) {
        CustomUser user = new CustomUser(userDto.getFirstName(),
                userDto.getLastName(), encodePassword(userDto.getPassword()),
                userDto.getUsername(), userDto.getAddress(), userDto.getGender(), createRoles(Roles.User.getRoleName()));
        return userRepository.save(user);
    }

    @Override
    public CustomUser getUser(String username) {
        return userRepository.findByEmail(username);
    }


    @Override
    public CustomUser updateUser(UserDto userDto) {
        //Validate userDto
        CustomUser currentUser = getCurrentUser();
        if (currentUser == null || !currentUser.getEmail().equals(userDto.getUsername())) {
            throw new InvalidArguments("Invalid Username");
        }
        //Update or Create User on database
        CustomUser storedUser = userRepository.findByEmail(currentUser.getEmail());
        if (storedUser == null) {
            storedUser = currentUser;
        }

        storedUser = userConverter.convert(userDto, storedUser);
        // Handler firstly created social login user
        if (storedUser.getAuthorities()
                .stream().anyMatch(authority -> authority.getAuthority().equals(Roles.Pre_User.getRoleName()))) {
            storedUser.removeRole(Roles.Pre_User.getRoleName());
            storedUser.addRole(Roles.User.getRoleName());
        }

        storedUser = userRepository.save(storedUser);

        //Update User on securityContext, web server cache !!
        setAuthentication(storedUser);
        //Return the user
        return storedUser;
    }

    @Override
    public boolean deleteUser(String username) {
        return false;
    }

    private Set<Role> createRoles(String... roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String role : roleNames) {
            roles.add(getRole(role));
        }
        return roles;
    }

    private Collection<SimpleGrantedAuthority> createAuthority(String... roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList();
        if (roles == null || roles.length == 0) {
            authorities.add(new SimpleGrantedAuthority(Roles.User.getRoleName()));
            return authorities;
        }

        return Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority(Roles.valueOf(role).getRoleName()))
                .collect(Collectors.toList());
    }

    private String encodePassword(String password) {
//        PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return passwordEncoder.encode(password);
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
//        return encoder.encode(password);
//        assertTrue(encoder.matches("myPassword", result));
    }

    @Override
    public Role getRole(String roleName) {
        return roleRepository.findByName(roleName).orElse(new Role(roleName));
    }
}

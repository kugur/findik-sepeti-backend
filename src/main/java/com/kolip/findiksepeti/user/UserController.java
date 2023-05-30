package com.kolip.findiksepeti.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class.getName());
    private final UserService userService;
    private final CustomOauth2UserService oauth2UserService;

    public UserController(UserService userService, CustomOauth2UserService oauth2UserService) {
        this.userService = userService;
        this.oauth2UserService = oauth2UserService;
    }

    @GetMapping("/users")
    public CustomUser getUser(@CurrentUser CustomUser user) {

        if (user == null &&
                SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2AuthenticationToken) {
            user = oauth2UserService.getCurrentUser();
        }
        logger.info("current user {0}", user);
        return user;
    }

    @PostMapping(value = "/users")
    public CustomUser createUser(@RequestBody UserDto user) {
        logger.info("user that wanted to be creted  {}", user);
        return userService.createUser(user);
    }

    @PutMapping(value = "/users")
    public CustomUser updateUser(@RequestBody UserDto user) {
        logger.info("user that  wanted to be updated {}", user);
        return userService.updateUser(user);
    }
}

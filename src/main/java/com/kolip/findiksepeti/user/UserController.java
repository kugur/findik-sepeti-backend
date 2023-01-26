package com.kolip.findiksepeti.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class.getName());

    @GetMapping("/users")
    public CustomUser getUser(@CurrentUser CustomUser user) {
        Object currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("current user {0}", user);
        return user;
    }

    @PostMapping(value = "/users")
    public ResponseEntity createUser(@RequestBody UserDto user) {
        logger.info("user that wanted to be creted  %s", user);
        return ResponseEntity.noContent().build();
    }
}

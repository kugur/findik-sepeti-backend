package com.kolip.findiksepeti.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class UserController {
    @GetMapping("/users")
    public User getUser() {
        Object currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new User("ugurkol", "ugur", "kol");
    }
}

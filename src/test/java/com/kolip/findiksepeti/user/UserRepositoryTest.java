package com.kolip.findiksepeti.user;

import org.h2.engine.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void save_WithOneRole_ShouldNotPersistRole() {

    }

    private CustomUser persistUser() {
        CustomUser user = new CustomUser("uur", "klp", "1234", "uur@gmail.com", "adres alani", Gender.MALE,
                Set.of(new Role("ROLE_USER")));
        user = userRepository.saveAndFlush(user);
        return user;
    }

}
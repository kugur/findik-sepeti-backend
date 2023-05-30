package com.kolip.findiksepeti.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<CustomUser, Long> {
    CustomUser findByEmail(String username);
}

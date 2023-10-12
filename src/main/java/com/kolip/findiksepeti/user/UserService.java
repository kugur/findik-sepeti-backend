package com.kolip.findiksepeti.user;

public interface UserService {

    CustomUser createUser(UserDto userDto);

    CustomUser getCurrentUser();

    CustomUser getUser(String username);

    CustomUser updateUser(UserDto userDto);

    boolean deleteUser(String username);

    Role getRole(String role);
}

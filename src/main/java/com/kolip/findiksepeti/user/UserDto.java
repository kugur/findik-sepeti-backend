package com.kolip.findiksepeti.user;

/**
 * Created by ugur.kolip
 */
public class UserDto {
    //TODO(ugur) gerek var mi ? Dogrudan CustomUser mi kullansak ?
    public String username;
    public String password;
    public String address;
    public String name;
    public String lastName;


    public UserDto() {
    }

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDto(String username, String password, String address, String name, String lastName) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.name = name;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDto{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", address='" +
                address + '\'' + ", name='" + name + '\'' + ", lastName='" + lastName + '\'' + '}';
    }
}

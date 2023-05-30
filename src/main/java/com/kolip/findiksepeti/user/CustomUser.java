package com.kolip.findiksepeti.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolip.findiksepeti.common.AbstractEntity;
import jakarta.persistence.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "custom_user")
public class    CustomUser extends AbstractEntity {

    private String firstName;
    private String lastName;
    @JsonIgnore
    private String password;
    private String email;
    private String address;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Transient
    private Collection<SimpleGrantedAuthority> authorities;

    public CustomUser() {
    }

//    public CustomUser(String firstName, String lastName, String password, String email,
//                      Collection<SimpleGrantedAuthority> authorities) {
//        this(firstName, lastName, password, email, "", authorities);
//    }

    public CustomUser(String firstName, String lastName, String password, String email, String address,
                      Gender gender,
                      Collection<SimpleGrantedAuthority> authorities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.authorities = authorities;

        //Set Roles
        if (this.authorities != null) {
            setAuthorities(this.authorities);
        }
    }

    public Collection<SimpleGrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
        }
        return authorities;
    }

    public void setAuthorities(Collection<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
        if (roles == null) {
            roles = new HashSet<>();
        }
        authorities.stream().forEach(authority -> {
            roles.add(new Role(authority.getAuthority()));
        });
    }

    public void addRole(String roleToBeAdded) {
        boolean roleExist = roles.stream().anyMatch(role -> role.getName().equals(roleToBeAdded));
        if (!roleExist) {
            roles.add(new Role(roleToBeAdded));
            authorities.add(new SimpleGrantedAuthority(roleToBeAdded));
        }
    }

    public void removeRole(String roleToBeRemoved) {
        authorities.removeIf(storedAuthority -> storedAuthority.getAuthority().equals(roleToBeRemoved));
        roles.removeIf(role -> role.getName().equals(roleToBeRemoved));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "CustomUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", roles=" + roles +
                ", authorities=" + authorities +
                '}';
    }
}

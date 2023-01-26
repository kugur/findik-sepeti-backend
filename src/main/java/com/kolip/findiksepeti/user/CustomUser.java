package com.kolip.findiksepeti.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolip.findiksepeti.common.AbstractEntity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "custom_user")
public class CustomUser extends AbstractEntity {

    private String firstName;
    private String lastName;
    @JsonIgnore
    private String password;
    private String email;
    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUser() {
    }

    public CustomUser(String firstName, String lastName, String password, String email,
                      Collection<? extends GrantedAuthority> authorities) {
        this(firstName, lastName, password, email, "", authorities);
    }

    public CustomUser(String firstName, String lastName, String password, String email, String address,
                      Collection<? extends GrantedAuthority> authorities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.authorities = authorities;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
        }
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
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
}

package com.kolip.findiksepeti.user;

import com.kolip.findiksepeti.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Role extends AbstractEntity {

    String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

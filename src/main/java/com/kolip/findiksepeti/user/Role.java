package com.kolip.findiksepeti.user;

import com.kolip.findiksepeti.common.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class Role extends AbstractEntity {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

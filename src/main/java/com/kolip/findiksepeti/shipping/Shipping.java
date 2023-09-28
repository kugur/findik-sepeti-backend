package com.kolip.findiksepeti.shipping;

import com.kolip.findiksepeti.common.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class Shipping extends AbstractEntity {
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Shipping{" + "address='" + address + '\'' + '}';
    }
}

package com.kolip.findiksepeti.shipping;

import com.kolip.findiksepeti.common.AbstractEntity;
import com.kolip.findiksepeti.order.Order;
import jakarta.persistence.*;

@Entity
public class Shipping extends AbstractEntity {
    @Id
    private Long id;

    private String address;
    private String note;
    private String name;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.common.AbstractEntity;
import com.kolip.findiksepeti.products.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class OrderItem extends AbstractEntity {
    @OneToOne
    private Product product;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" + "product=" + product + ", quantity=" + quantity + '}';
    }
}

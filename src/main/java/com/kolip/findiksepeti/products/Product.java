package com.kolip.findiksepeti.products;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Creted by ugur.kolip on 10/07/2023.
 * Product entity consist of product values.
 */
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @Column(precision = 10, scale = 2, nullable = true)
    private BigDecimal price = BigDecimal.ZERO;
    private String imageUrl;
    private String category;
    @Column(length = 2048)
    private String description;

    public Product() {
    }

    public Product(String name, int price, String imageUrl) {
        this(name, BigDecimal.valueOf(price), imageUrl, "", "");
    }

    public Product(String name, BigDecimal price, String imageUrl) {
        this(name, price, imageUrl, "", "");
    }

    public Product(String name, BigDecimal price, String imageUrl, String category, String description) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name='" + name + '\'' + ", price=" + price + ", imageUrl='" + imageUrl +
                '\'' + ", category='" + category + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(getId(), product.getId()) && Objects.equals(getName(), product.getName()) &&
                Objects.equals(getPrice(), product.getPrice()) &&
                Objects.equals(getImageUrl(), product.getImageUrl()) &&
                Objects.equals(getCategory(), product.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPrice(), getImageUrl(), getCategory());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

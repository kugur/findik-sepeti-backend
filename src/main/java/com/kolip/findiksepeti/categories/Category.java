package com.kolip.findiksepeti.categories;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kolip.findiksepeti.products.Product;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ugur.kolip on 10/07/2023
 * Category Entity
 */
@Entity
@SequenceGenerator(name = "category_seq", sequenceName = "category_seq", initialValue = 10, allocationSize = 10)
public class Category implements Cloneable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    public Long id;

    public Category() {
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }


    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    @Override
    protected Category clone() throws CloneNotSupportedException {
        return (Category) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;

        if (getId() != null ? !getId().equals(category.getId()) : category.getId() != null) return false;
        return getName() != null ? getName().equals(category.getName()) : category.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}

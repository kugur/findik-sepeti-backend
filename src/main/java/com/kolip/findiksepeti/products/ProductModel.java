package com.kolip.findiksepeti.products;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by ugur.kolip
 * Product Model for formData with image data.
 */
public class ProductModel extends Product {
    private MultipartFile imageFile;

    public ProductModel() {
    }

    public ProductModel(String name, BigDecimal price, String imageUrl, String category, MultipartFile imageFile) {
        super(name, price, imageUrl, category);
        this.imageFile = imageFile;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductModel that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getImageFile(), that.getImageFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getImageFile());
    }
}

package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.categories.Category;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * Created by ugur.kolip
 * Product Model for formData with image data.
 */
public class ProductModel {
    private Long id;
    private MultipartFile imageFile;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Category category;
    private String description;
    private boolean imageFileChanged;

    public ProductModel() {
    }

    public ProductModel(String name, BigDecimal price, String imageUrl, Category category, MultipartFile imageFile,
                        String description) {
        super();
        this.imageFile = imageFile;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
        this.imageFileChanged = imageFile != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
        imageFileChanged = imageFile != null;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isImageFileChanged() {
        return imageFileChanged;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageFileChanged(boolean imageFileChanged) {
        this.imageFileChanged = imageFileChanged;
    }

    public void setCategoryId(Long id) {
        if (category == null) {
            category = new Category();
        }
        category.setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductModel that)) return false;

        if (isImageFileChanged() != that.isImageFileChanged()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getImageFile() != null ? !getImageFile().equals(that.getImageFile()) : that.getImageFile() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getPrice() != null ? !getPrice().equals(that.getPrice()) : that.getPrice() != null) return false;
        if (getImageUrl() != null ? !getImageUrl().equals(that.getImageUrl()) : that.getImageUrl() != null) {
            return false;
        }
        if (getCategory() != null ? !getCategory().equals(that.getCategory()) : that.getCategory() != null) {
            return false;
        }
        return getDescription() != null ? getDescription().equals(that.getDescription()) :
                that.getDescription() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getImageFile() != null ? getImageFile().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        result = 31 * result + (getImageUrl() != null ? getImageUrl().hashCode() : 0);
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (isImageFileChanged() ? 1 : 0);
        return result;
    }
}

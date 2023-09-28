package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.categories.Category;

import java.math.BigDecimal;

public class ProductGenerator {

    public static Product createProduct(Long productId) {
        Category category = new Category(10L, "processed");
        Product product =
                new Product("findik ezmesi", BigDecimal.valueOf(10L), "imageUrl/raw.png", category, "aciklama");
        product.setId(productId);
        return product;
    }
}

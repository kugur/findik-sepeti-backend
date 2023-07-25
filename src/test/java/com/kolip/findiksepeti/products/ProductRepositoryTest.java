package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.categories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;


    @Test
    public void save_ProductWithCategoryIdAndNameNull_ShouldPersistProductNotUpdateCategory() {
        //Initialize
        String productName = "raw nuts";
        Category category = categoryRepository.save(new Category(1L, "raw"));
        Product product = new Product();
        product.setCategory(new Category(category.getId(), "asdf"));
        product.setName(productName);

        //Run Test
        Product persistedProduct = productRepository.save(product);
        Category categoryAfterProductPersisted = categoryRepository.findById(category.getId()).get();

        //Verify Result
        assertEquals(Long.valueOf(1L), persistedProduct.getCategory().getId());
        assertEquals(category.getName(), categoryAfterProductPersisted.getName(),
                "Product product update, category values should not be updated.");
        assertEquals(productName, persistedProduct.getName());
    }

    @Test
    public void save_ProductUpdatedWithNewNameAndCategory_ShouldBePersistedNewValues() {
        //Initialize
        String newName = "new ProductName";
        String oldName = "old ProductName";
        Category oldCategory = categoryRepository.save(new Category(null, "oldCategory"));
        Category newCategory = categoryRepository.save(new Category(null, "newCategory"));
        Product willBeUpdatedProduct = productRepository.save(new Product(oldName, BigDecimal.valueOf(11.0), "old_url", oldCategory, "old Description"));

        //Run Test
        willBeUpdatedProduct.setCategory(newCategory);
        willBeUpdatedProduct.setName(newName);
        Product updatedProduct = productRepository.save(willBeUpdatedProduct);

        //Verify Result
        assertEquals(newCategory.getName(), updatedProduct.getCategory().getName());
        assertEquals(newName, updatedProduct.getName());
    }
}

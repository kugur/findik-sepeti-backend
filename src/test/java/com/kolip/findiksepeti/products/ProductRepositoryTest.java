package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.categories.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void findAll_ProductAll_ShouldNotCreateAnotherScirptsForCategories() {
        //Initialize
        List<Long> ids = createProductAndCategories();

        //Run Test
        System.out.println("productRepository.findAll() script");
        List<Product> result = productRepository.findAll();

        //Verify Result
         assertEquals(3, result.size());
         assertNotNull(result.get(0).getCategory());
         assertNotNull(result.get(0).getCategory().getName());

    }

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

    private List<Long> createProductAndCategories() {
        Category category1 = new Category(null, "raw");
        Category category2 = new Category(null, "processed");
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.flush();

        Product product1 = new Product("product1", BigDecimal.valueOf(11), "product1_url/", category1, "product1 description");
        Product product11 = new Product("product11", BigDecimal.valueOf(11), "product11_url/", category1, "product11 description");
        Product product2 = new Product("product2", BigDecimal.valueOf(22), "product2_url/", category2, "product2 description");
        Product saved1 = productRepository.saveAndFlush(product1);
        Product saved2 = productRepository.saveAndFlush(product11);
        Product saved3 = productRepository.saveAndFlush(product2);

        return Arrays.asList(saved1.getId(), saved2.getId(), saved3.getId());
    }
}

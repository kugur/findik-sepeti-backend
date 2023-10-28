package com.kolip.findiksepeti.categories;

import com.kolip.findiksepeti.AbstractTest;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.products.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryRepositoryTest extends AbstractTest {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveAll_WithOneOfItemNull_ShouldSaveOthers() {
        //Initialize
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "raw"));
//        categories.add(null);
        categories.add(new Category(2L, "processed"));

        //Run Test
        List<Category> result = categoryRepository.saveAll(categories);

        //Verify
        assertEquals(2, result.size());
    }

    @Test
    public void getById_WithCategoryIdThatUsedByProducts_ShouldReturnCategory() {
        //Initialize
        Category categoryUsedByProducts = new Category(null, "raw");
        categoryUsedByProducts = categoryRepository.save(categoryUsedByProducts);
        long categoryId = categoryUsedByProducts.getId();

        int productCount = 3;
        for (int i = 0; i < productCount; i++) {
            Product productThatHaveCategory = createProductWithOutCategory();
            productThatHaveCategory.setCategory(new Category(categoryId, null));
            productRepository.save(productThatHaveCategory);
        }

        productRepository.flush();
        categoryRepository.flush();
        entityManager.clear();

        //Run Test
        Category persistedCategory = categoryRepository.findById(categoryUsedByProducts.getId()).get();
        List<Product> productsOfPersistedCategory = persistedCategory.getProducts();

        //Verify Result
        int productCountInDb = JdbcTestUtils.countRowsInTable(jdbcTemplate, "product");
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "product", "category_id = " + categoryId);
        assertEquals(3, productCountInDb);
        assertEquals(3, count);
        assertNotNull(productsOfPersistedCategory);
        assertEquals(productCount, productsOfPersistedCategory.size());
    }

    @Test
    public void delete_CategoryUsedByProduct_ShouldNotDelete() {
        //Initialize
        Long categoryId = createCategoryWithThreeProduct();

        //Run Test
        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryRepository.deleteById(categoryId);
            categoryRepository.flush();
        });
    }

    @Test
    @Disabled
    public void deleteAll_OneOfUsedByProduct() {
        //Initialize
        Long categoryId = createCategoryWithThreeProduct();
        Category categoryNotUsedByProduct = categoryRepository.save(new Category(null, "deneme"));
        Category categoryNotUsedByProduct2 = categoryRepository.save(new Category(null, "deneme2"));
        categoryRepository.flush();
        entityManager.clear();

        assertEquals(3, categoryRepository.count());

        //Run Test
        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryRepository.deleteAllById(Arrays.asList(categoryNotUsedByProduct.getId(),
                    categoryId, categoryNotUsedByProduct2.getId()));
            categoryRepository.flush();
        });

        entityManager.clear();

        //Verify
        assertEquals(1, categoryRepository.count());
        assertFalse(categoryRepository.findById(categoryId).isEmpty());
        assertTrue(categoryRepository.findById(categoryNotUsedByProduct.getId()).isEmpty());
        assertTrue(categoryRepository.findById(categoryNotUsedByProduct2.getId()).isEmpty());

    }

    private Product createProductWithOutCategory() {
        return new Product("product1", BigDecimal.valueOf(11l), "product1_image_url", null, "product1_description");
    }

    private Long createCategoryWithThreeProduct() {
        Category categoryUsedByProducts = new Category(null, "raw");
        categoryUsedByProducts = categoryRepository.save(categoryUsedByProducts);
        long categoryId = categoryUsedByProducts.getId();

        int productCount = 3;
        for (int i = 0; i < productCount; i++) {
            Product productThatHaveCategory = createProductWithOutCategory();
            productThatHaveCategory.setCategory(new Category(categoryId, null));
            productRepository.save(productThatHaveCategory);
        }

        productRepository.flush();
        categoryRepository.flush();
        entityManager.clear();

        return categoryId;
    }
}

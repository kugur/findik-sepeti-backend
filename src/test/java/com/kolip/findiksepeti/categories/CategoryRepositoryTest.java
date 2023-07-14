package com.kolip.findiksepeti.categories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DataJpaTest
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

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
}

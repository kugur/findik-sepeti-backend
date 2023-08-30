package com.kolip.findiksepeti.categories;

import com.kolip.findiksepeti.common.DeleteResponse;
import com.kolip.findiksepeti.common.Errors;
import com.kolip.findiksepeti.common.UpdateResponse;
import com.kolip.findiksepeti.products.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CategoryServiceImplTest.class)
class CategoryServiceImplTest {

    private CategoryService instanceUnderTest;

    @MockBean
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        instanceUnderTest = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    public void getCategories_WithoutParameter_ReturnCategories() {
        //Initialize
        List<Category> persistedCategories = createCategoryList();

        when(categoryRepository.findAll()).thenReturn(persistedCategories);

        //Run Test
        List<Category> result = instanceUnderTest.getCategories();

        //Verify Result
        assertEquals(persistedCategories, result);
        verify(categoryRepository).findAll();
    }

    @Test
    public void addCategories_WithCategories_ReturnTrue() {
        //Initialize
        Category willBePersistedCategory = createCategoryListWithOutId().get(0);
        when(categoryRepository.save(any())).thenReturn(createCategoryList().get(0));

        //Run Test
        UpdateResponse<Category> result = instanceUnderTest.addCategories(willBePersistedCategory);

        //Verify Result
        verify(categoryRepository).save(any());
        assertTrue(result.isSuccessful());
    }

    @Test
    public void addCategories_WithNullValues_ReturnFalse() {
        //Initialize
        Category category = null;

        //Run Test
        UpdateResponse<Category> result = instanceUnderTest.addCategories(category);

        //Verify Result
        verify(categoryRepository, times(0)).saveAll(any());
        assertFalse(result.isSuccessful());
    }

    @Test
    public void addCategories_WithNotNullIds_SetIdsNullAndReturnTrue() {
        //Initialize
        Category category = createCategoryList().get(0);
        when(categoryRepository.save(any())).thenReturn(category);

        //Run Test
        UpdateResponse<Category> result = instanceUnderTest.addCategories(category);

        //Verify Result
        verify(categoryRepository).save(any());
        verify(categoryRepository).save(argThat(argument -> argument.getId() == null));
        assertTrue(result.isSuccessful());
    }

    @Test
    public void deleteCategories_WithIds_ShouldCallRepositoryWithIds() {
        //Initialize
        List<Long> ids = createThreeIds();

        //Run Test
        DeleteResponse result = instanceUnderTest.deleteCategories(ids);

        //Verify Result
        assertTrue(result.isSuccessful());
        verify(categoryRepository, times(3)).deleteById(anyLong());
    }

    @Test
    public void deleteCategories_DeleteCategoriesUsedByProducts_ShouldNotDeleteCategories() {
        //Initialize`
        List<Long> ids = createThreeIds();
        doThrow(new DataIntegrityViolationException("")).when(categoryRepository).deleteById(eq(ids.get(1)));

        //Run Test
        DeleteResponse deleteResponse = instanceUnderTest.deleteCategories(ids);

        //Verify Result
        assertEquals(1, deleteResponse.getCouldNotDeleteResults().size());
        assertEquals(Errors.INVALID_ARGUMENT.description, deleteResponse.getCouldNotDeleteResults().get(ids.get(1)));
        verify(categoryRepository, times(3)).deleteById(any());
    }

    @Test
    public void update_WithValidValues_ShouldUpdateCategory() {
        //Initialize
        Category willBeUpdated = new Category(1L, "new Value");
        when(categoryRepository.save(eq(willBeUpdated))).thenReturn(willBeUpdated);

        //Run Test
        UpdateResponse<Category> response = instanceUnderTest.update(willBeUpdated);

        //Verify Result
        verify(categoryRepository).save(willBeUpdated);
        assertTrue(response.isSuccessful());
        assertEquals(willBeUpdated, response.getUpdatedValue());
    }

    @Test
    public void update_WithInvalidValue_ShouldReturnError() {
        //Initialize
        Category willBeUpdatedCategory = null;
        when(categoryRepository.save(isNull())).thenThrow(IllegalArgumentException.class);

        //Run Test
        UpdateResponse<Category> response = instanceUnderTest.update(willBeUpdatedCategory);

        //Verify Result
        verify(categoryRepository).save(willBeUpdatedCategory);
        assertFalse(response.isSuccessful());
        assertEquals(Errors.INVALID_ARGUMENT.description, response.getErrorMessage());
    }

    private boolean isAllIdsNull(Iterator<Category> categoryIterator) {
        boolean allIdsNull = true;
        while (categoryIterator.hasNext()) {
            if (categoryIterator.next().getId() != null) {
                allIdsNull = false;
                break;
            }
        }
        return allIdsNull;
    }

    private List<Long> createThreeIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        return ids;
    }

    private List<Category> createCategoryList() {
        List<Category> categories = new LinkedList<>();
        categories.add(new Category(1L, "raw"));
        categories.add(new Category(2L, "processed"));
        return categories;
    }

    private List<Category> createCategoryListWithOutId() {
        List<Category> categories = new LinkedList<>();
        categories.add(new Category(null, "raw"));
        categories.add(new Category(null, "processed"));
        return categories;
    }

    private Product createProduct(Category category) {
        Product product = new Product("product1", BigDecimal.valueOf(11L), "imageUrl", category, "description");
        return product;
    }

}
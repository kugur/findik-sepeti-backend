package com.kolip.findiksepeti.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
        List<Category> willBePersistedCategories = createCategoryListWithOutId();
        when(categoryRepository.saveAll(any())).thenReturn(createCategoryList());

        //Run Test
        boolean result = instanceUnderTest.addCategories(willBePersistedCategories);

        //Verify Result
        verify(categoryRepository).saveAll(any());
        assertTrue(result);
    }

    @Test
    public void addCategories_WithNullValues_ReturnFalse() {
        //Initialize
        List<Category> categories = null;

        //Run Test
        boolean result = instanceUnderTest.addCategories(categories);

        //Verify Result
        verify(categoryRepository, times(0)).saveAll(any());
        assertFalse(result);
    }

    @Test
    public void addCategories_WithNotNullIds_SetIdsNullAndReturnTrue() {
        //Initialize
        List<Category> categories = createCategoryList();
        when(categoryRepository.saveAll(any())).thenReturn(categories);

        //Run Test
        boolean result = instanceUnderTest.addCategories(categories);

        //Verify Result
        verify(categoryRepository).saveAll(anyList());
        verify(categoryRepository).saveAll(argThat(argument -> isAllIdsNull(argument.iterator())));
        assertTrue(result);
    }

    @Test
    public void addCategories_WithOneOfItemNull_ShouldSaveOthersAndReturnTrue() {
        //Initialize
        List<Category> categories = createCategoryList();
        List<Category> categoriesWithNull = createCategoryList();
        categoriesWithNull.add(null);
        ArgumentCaptor<List<Category>> saveArgumentCaptor = ArgumentCaptor.forClass(List.class);
        when(categoryRepository.saveAll(saveArgumentCaptor.capture())).thenReturn(categories);

        //Run Test
        boolean result = instanceUnderTest.addCategories(categoriesWithNull);

        //Verify Result
        verify(categoryRepository).saveAll(anyList());
        assertTrue(result);
        assertEquals(categories.size(), saveArgumentCaptor.getValue().size(), "null items should not be saved");
    }

    @Test
    public void deleteCategories_WithIds_ShouldCallRepositoryWithIds() {
        //Initialize
        List<Long> ids = createIds();

        //Run Test
        boolean result = instanceUnderTest.deleteCategories(ids);

        //Verify Result
        assertTrue(result);
        verify(categoryRepository).deleteAllByIdInBatch(eq(ids));
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

    private List<Long> createIds() {
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

}
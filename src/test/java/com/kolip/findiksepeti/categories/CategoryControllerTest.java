package com.kolip.findiksepeti.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CategoryServiceImpl.class)
class CategoryControllerTest {
    private MockMvc mockMvc;

    @MockBean
    public CategoryService categoryService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(categoryService)).build();
    }

    @Test
    public void getCategories_WithoutParameters_ShouldReturnCategories() throws Exception {
        //Initialize
        List<Category> categories = createCategories();
        when(categoryService.getCategories()).thenReturn(categories);

        //Run Test
        mockMvc.perform(get("/category")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(categories.size()));

        //Verify Result
        verify(categoryService).getCategories();
    }

    @Test
    public void addCategories_WithNewCategories_ShouldReturnTrue() throws Exception {
        //Initialize
        List<Category> willBePersistedCategories = createCategoriesWithoutId();
        when(categoryService.addCategories(any())).thenReturn(true);
        String requestedCategoriesJson = objectMapper.writeValueAsString(Arrays.asList(willBePersistedCategories));

        //Run test
        mockMvc.perform(post("/category").content(objectMapper.writeValueAsString(willBePersistedCategories))
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        //Verify Result
        verify(categoryService).addCategories(argThat(argument -> argument.size() == willBePersistedCategories.size()));
    }

    private List<Category> createCategories() {
        List<Category> categories = new LinkedList<>();
        categories.add(new Category(1L, "raw"));
        categories.add(new Category(2L, "processed"));
        return categories;
    }

    private List<Category> createCategoriesWithoutId() {
        List<Category> categories = new LinkedList<>();
        categories.add(new Category(null, "raw"));
        categories.add(new Category(null, "processed"));
        return categories;
    }

}
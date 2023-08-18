package com.kolip.findiksepeti.categories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolip.findiksepeti.common.DeleteResponse;
import com.kolip.findiksepeti.common.Errors;
import com.kolip.findiksepeti.common.UpdateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public void addCategories_WithNewCategories_ShouldReturnOK() throws Exception {
        //Initialize
        List<Category> willBePersistedCategories = createCategoriesWithoutId();
        when(categoryService.addCategories(any())).thenReturn(true);

        //Run test
        mockMvc.perform(post("/category").content(objectMapper.writeValueAsString(willBePersistedCategories))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        //Verify Result
        verify(categoryService).addCategories(argThat(argument -> argument.size() == willBePersistedCategories.size()));
    }

    @Test
    public void updateCategory_WithNewName_ShouldReturnUpdatedCategory() throws Exception {
        //Initialize
        Category willBeUpdatedCategory = new Category(1L, "raw");
        when(categoryService.update(eq(willBeUpdatedCategory))).thenReturn(new UpdateResponse<>(willBeUpdatedCategory));

        //Run Test
        mockMvc.perform(put("/category").content(objectMapper.writeValueAsString(willBeUpdatedCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Verify Result
        verify(categoryService).update(argThat(argument -> Objects.equals(argument.getId(),
                willBeUpdatedCategory.getId())));
    }

    @Test
    public void deneme() {
        //Initialize
        Category willBeUpdatedCategory = new Category(1L, "raw");
        String categoryJson = "";
        try {
            categoryJson = objectMapper.writeValueAsString(willBeUpdatedCategory);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertNotEquals("", categoryJson);
        Category result = null;
        try {
            result = objectMapper.readValue(categoryJson, Category.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertNotNull(result);
    }

    @Test
    public void updateCategory_WithInvalidValue_ShouldReturnInvalidError() throws Exception {
        //Initialize
        String requestBody = "{asdf: asdf}";

        //Run Test
        mockMvc.perform(put("/category").contentType(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCategories_WithIds_ShouldReturnTrue() throws Exception {
        //Initialize
        List<Long> willBeDeletedIds = createIds();
        when(categoryService.deleteCategories(anyList())).thenReturn(new DeleteResponse());

        //Run Test
        mockMvc.perform(delete("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", objectMapper.writeValueAsString(willBeDeletedIds)))
                .andExpect(status().isOk());

        //Verify Result
        verify(categoryService).deleteCategories(argThat(argument -> argument.size() == willBeDeletedIds.size()));
        verify(categoryService).deleteCategories(argThat(argument -> argument.stream().allMatch(id -> {
            return willBeDeletedIds.stream().anyMatch(willBeDeletedId -> willBeDeletedId == id);
        })));
    }

    @Test
    public void deleteCategories_WithoutIds_ShouldReturnTrue() throws Exception {
        //Initialize
        List<Long> willBeDeletedIds = new ArrayList<>();
        when(categoryService.deleteCategories(anyList())).thenReturn(new DeleteResponse());

        //Run Test
        mockMvc.perform(delete("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", objectMapper.writeValueAsString(willBeDeletedIds)))
                .andExpect(status().isOk());

        //Verify Result
        verify(categoryService).deleteCategories(argThat(argument -> argument.size() == 0));
    }

    @Test
    public void deleteCategories_InvalidIds_ShouldReturnFalse() throws Exception {
        //Initialize
        String ids = "asdf";

        //Run Test
        mockMvc.perform(delete("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.generalError").value(Errors.INVALID_ARGUMENT.description));

        //Verify
        verify(categoryService, times(0)).deleteCategories(anyList());
    }

    @Test
    public void deleteCategories_CouldNotDeleteSuccessfully_ShouldReturnFalse() throws Exception {
        //Initialize
        List<Long> ids = createIds();
        DeleteResponse deleteResponse = new DeleteResponse();
        deleteResponse.setGeneralError("General Error");
        when(categoryService.deleteCategories(anyList())).thenReturn(deleteResponse);

        //Run Test
        mockMvc.perform(delete("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generalError").value(deleteResponse.getGeneralError()));
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

    private List<Long> createIds() {
        List<Long> ids = new LinkedList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        return ids;
    }

}
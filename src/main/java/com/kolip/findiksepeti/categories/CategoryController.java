package com.kolip.findiksepeti.categories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolip.findiksepeti.common.DeleteResponse;
import com.kolip.findiksepeti.common.Errors;
import com.kolip.findiksepeti.common.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private CategoryService categoryService;
    private ObjectMapper mapper;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
        mapper = new ObjectMapper();
    }

    @GetMapping("/category")
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    @PostMapping("/category")
    public ResponseEntity<UpdateResponse<Category>> addCategories(@RequestBody Category category) {
         UpdateResponse<Category> result = categoryService.addCategories(category);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/category")
    public ResponseEntity<UpdateResponse<Category>> updateCategory(@RequestBody Category category) {

        UpdateResponse<Category> updatedCategory = categoryService.update(category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/category")
    public ResponseEntity<DeleteResponse> deleteCategories(@RequestParam(value = "ids") String jsonIds) {
        ArrayList<Long> ids = null;
        try {
            ids = mapper.readValue(jsonIds,
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Long.class));
        } catch (JsonProcessingException e) {
            logger.error("Exception occurred while deleting Category. Exception: {}", e.getMessage());
            return new ResponseEntity<>(new DeleteResponse(Errors.INVALID_ARGUMENT.description), HttpStatus.BAD_REQUEST);
        }

        DeleteResponse result = categoryService.deleteCategories(ids);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}

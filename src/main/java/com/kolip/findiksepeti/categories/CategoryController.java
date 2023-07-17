package com.kolip.findiksepeti.categories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ResponseEntity<Boolean> addCategories(@RequestBody ArrayList<Category> categories) {
        boolean result = categoryService.addCategories(categories);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/category")
    public ResponseEntity<Boolean> deleteCategories(@RequestParam(value = "ids") String jsonIds) {
        ArrayList<Long> ids = null;
        try {
            ids = mapper.readValue(jsonIds,
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Long.class));
        } catch (JsonProcessingException e) {
            logger.error("Exception occurred while deleting Category. Exception: {}", e.getMessage());
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        boolean result = categoryService.deleteCategories(ids);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

package com.kolip.findiksepeti.categories;

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

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
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
}

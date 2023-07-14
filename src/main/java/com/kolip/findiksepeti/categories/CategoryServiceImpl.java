package com.kolip.findiksepeti.categories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public boolean addCategories(List<Category> categories) {
        if (categories == null) {
            return false;
        }
        categories = categories.stream().filter(Objects::nonNull).toList();
        setIdsNull(categories);
        categoryRepository.saveAll(categories);
        return true;
    }

    private void setIdsNull(List<Category> categories) {
        categories.forEach(category -> {
            if (category != null && category.getId() != null) {
                logger.warn("Try to be added category with id is not null category: {}", category);
                category.setId(null);
            }
        });
    }
}

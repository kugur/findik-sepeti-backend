package com.kolip.findiksepeti.categories;

import com.kolip.findiksepeti.common.DeleteResponse;
import com.kolip.findiksepeti.common.Errors;
import com.kolip.findiksepeti.common.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public UpdateResponse<Category> update(Category category) {
        try {
            Category updatedCategory = categoryRepository.save(category);
            return new UpdateResponse<>(updatedCategory);
        } catch (IllegalArgumentException argumentException) {
            return new UpdateResponse<>(Errors.INVALID_ARGUMENT.description);
        } catch (Exception e) {
            return new UpdateResponse<>(Errors.UNKNOWN_ERROR.description);
        }
    }

    @Override
    public DeleteResponse deleteCategories(List<Long> ids) {
        DeleteResponse deleteResponse = new DeleteResponse();
        ids.forEach(id -> {
            String errorMessage = deleteById(id);
            if (StringUtils.hasText(errorMessage)) {
                deleteResponse.addNotDeletedResponse(id, errorMessage);
            }
        });

        return deleteResponse;
    }

    private String deleteById(Long id) {
        String errorMessage = "";
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            errorMessage = Errors.INVALID_ARGUMENT.description;
        } catch (Exception e) {
            logger.error("Exception occurred while deleting category by id: {} exception e: {}",
                    id, e.getMessage());
            errorMessage = Errors.UNKNOWN_ERROR.description;
        }
        return errorMessage;
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

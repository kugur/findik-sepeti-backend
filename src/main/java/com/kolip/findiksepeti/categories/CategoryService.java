package com.kolip.findiksepeti.categories;

import com.kolip.findiksepeti.common.DeleteResponse;
import com.kolip.findiksepeti.common.UpdateResponse;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();

    boolean addCategories(List<Category> categories);

    DeleteResponse deleteCategories(List<Long> anyList);

    UpdateResponse<Category> update(Category category);
}

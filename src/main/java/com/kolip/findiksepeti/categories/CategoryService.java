package com.kolip.findiksepeti.categories;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();

    boolean addCategories(List<Category> categories);

    boolean deleteCategories(List<Long> anyList);
}

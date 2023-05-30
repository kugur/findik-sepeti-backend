package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.filters.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    Page<Product> getProducts(List<Filter> filters, PageRequest pageRequest);
}

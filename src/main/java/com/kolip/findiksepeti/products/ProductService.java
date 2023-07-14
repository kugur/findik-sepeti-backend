package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.filters.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    Page<Product> getProducts(List<Filter> filters, PageRequest pageRequest);

    Product getProduct(long id);

    Product createProduct(ProductModel productModel);

    Product update(ProductModel productModel);

    boolean delete(Long id);
}

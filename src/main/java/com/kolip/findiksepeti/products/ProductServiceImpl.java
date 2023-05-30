package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.specifiation.SpecificationFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class
ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private SpecificationFactory specificationFactory;

    public ProductServiceImpl(ProductRepository productRepository, SpecificationFactory specificationFactory) {
        this.productRepository = productRepository;
        this.specificationFactory = specificationFactory;
    }

    //TODO(ugur) genericleri kullanamaya calis !!!!!!!!!
    @Override
    public Page<Product> getProducts(List<Filter> filters, PageRequest pageRequest) {
        Specification<Product> specification = specificationFactory.getSpecification(filters, Product.class);
        return productRepository.findAll(specification, pageRequest);
    }
}

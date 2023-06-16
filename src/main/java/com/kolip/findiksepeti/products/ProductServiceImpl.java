package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.specifiation.SpecificationFactory;
import com.kolip.findiksepeti.image.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private SpecificationFactory specificationFactory;
    private StorageService storageService;

    public ProductServiceImpl(ProductRepository productRepository, SpecificationFactory specificationFactory,
                              StorageService storageService) {
        this.productRepository = productRepository;
        this.specificationFactory = specificationFactory;
        this.storageService = storageService;
    }

    //TODO(ugur) genericleri kullanamaya calis !!!!!!!!!
    @Override
    public Page<Product> getProducts(List<Filter> filters, PageRequest pageRequest) {
        Specification<Product> specification = specificationFactory.getSpecification(filters, Product.class);
        return productRepository.findAll(specification, pageRequest);
    }

    @Override
    public Product getProduct(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    @Override
    public Product createProduct(ProductModel productModel) {
        String imageUrl = storageService.storeFile(productModel.getImageFile());
        productModel.setImageUrl(imageUrl);
        return productRepository.save(productModel);
    }
}

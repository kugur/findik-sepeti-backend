package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.exceptions.InvalidArguments;
import com.kolip.findiksepeti.exceptions.StorageException;
import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.specifiation.SpecificationFactory;
import com.kolip.findiksepeti.image.StorageService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final SpecificationFactory specificationFactory;
    private final StorageService storageService;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, SpecificationFactory specificationFactory,
                              StorageService storageService, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.specificationFactory = specificationFactory;
        this.storageService = storageService;
        this.modelMapper = modelMapper;
    }

    //TODO(ugur) genericleri kullanamaya calis !!!!!!!!!
    @Override
    public Page<Product> getProducts(List<Filter> filters, PageRequest pageRequest) {
        Specification<Product> specification = specificationFactory.getSpecification(filters, Product.class);
        Page<Product> result =  productRepository.findAll(specification, pageRequest);
        return result;
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
        Product product = modelMapper.map(productModel, Product.class);
        return productRepository.save(product);
    }

    @Override
    public boolean delete(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            logger.warn("Product is try to be deleted but it does not exist, for id: [{}]", id);
        }

        boolean imageFileDeletedSuccessfully = storageService.delete(product.get().getImageUrl());
        if (!imageFileDeletedSuccessfully) {
            logger.warn("Image file could not be deleted for url '{}' and product id: [{}] ",
                        product.get().getImageUrl(), id);
        }
        productRepository.delete(product.get());
        return true;
    }

    @Override
    public Product update(ProductModel productModel) {
        Product product = modelMapper.map(productModel, Product.class);

        if (!productModel.isImageFileChanged()) {
            return productRepository.save(product);
        }

        if (productModel.getImageFile() == null) {
            logger.error("ImageUrl is null, could not be updated. Id: {}", productModel.getId());
            throw new InvalidArguments("ImageUrl is null");
        }

        String imageUrl = storageService.storeFile(productModel.getImageFile());
        if (imageUrl == null) {
            throw new StorageException("Could not stored image");
        }

        boolean previousImageDeleted = storageService.delete(product.getImageUrl());
        if (!previousImageDeleted) {
            logger.warn("Could not deleted previous image url: {}", product.getImageUrl());
        }
        product.setImageUrl(imageUrl);

        return productRepository.save(product);
    }
}

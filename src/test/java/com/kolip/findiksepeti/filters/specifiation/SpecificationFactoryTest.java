package com.kolip.findiksepeti.filters.specifiation;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.FilterOperations;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.products.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
class SpecificationFactoryTest {

    private SpecificationFactory instanceUnderTest;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        instanceUnderTest = new SpecificationFactory();
    }

    @Test
    public void getSpecification_WithEmptyFilter_ShouldReturnProductSpecification() {
        //Initialize
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<Filter> filters = new ArrayList<>();

        Product product = new Product("raw", 11, "/imageUrl");
        productRepository.save(product);
        Product product2 = new Product("processed", 100, "/image-processed");
        productRepository.save(product2);

        //Run Test
        Specification<Product> specification = instanceUnderTest.getSpecification(filters, Product.class);
        Page<Product> result = productRepository.findAll(specification, pageRequest);

        //Verify
        assertEquals(2, result.getTotalElements(),
                "Specification for empty filter, should return all element");
    }

    @Test
    public void getSpecification_WithEqualFilter_ShouldReturnSpecificationForOneElement() {
        //Initialize
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<Filter> filters = new ArrayList(
                Arrays.asList(new Filter("name", FilterOperations.EQUAL, "sekerli")));

        Product product = new Product("sekersiz", 11, "/imageUrl");
        productRepository.save(product);
        Product product2 = new Product("sekerli", 100, "/image-processed");
        productRepository.save(product2);
        Product product3 = new Product("tuzlu", 30, "/image-processed");
        productRepository.save(product2);

        //Run Test
        Specification<Product> specification = instanceUnderTest.getSpecification(filters, Product.class);
        Page<Product> result = productRepository.findAll(specification, pageRequest);

        //Verify
        assertEquals(1, result.getTotalElements(),
                "Specification for one filter, should return one element");
        assertEquals(product2, result.getContent().get(0), "Returned element should be sekerli product");
    }

    @Test
    public void getSpecification_WithGreaterFilter_ShouldReturnSpecificationForTwoElement() {
        //Initialize
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<Filter> filters = new ArrayList(
                Arrays.asList(new Filter("price", FilterOperations.GREATER, "1")));

        Product product1 = new Product("sekersiz", 1, "/imageUrl");
        productRepository.save(product1);
        Product product2 = new Product("sekerli", 2, "/image-processed");
        productRepository.save(product2);
        Product product3 = new Product("tuzlu", 3, "/image-processed");
        productRepository.save(product3);

        //Run Test
        Specification<Product> specification = instanceUnderTest.getSpecification(filters, Product.class);
        Page<Product> result = productRepository.findAll(specification, pageRequest);

        //Verify
        assertEquals(2, result.getTotalElements(),
                "Specification for greater filter, should return two element");
        assertTrue(result.stream().anyMatch(product2::equals), "Should return product with price 2");
        assertTrue(result.stream().anyMatch(product3::equals), "Should return product with price 3");
    }

    @Test
    public void getSpecification_WithLessFilter_ShouldReturnSpecificationForTwoElement() {
        //Initialize
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<Filter> filters = new ArrayList(
                Arrays.asList(new Filter("price", FilterOperations.LOWER, "3")));

        Product product1 = new Product("sekersiz", 1, "/imageUrl");
        productRepository.save(product1);
        Product product2 = new Product("sekerli", 2, "/image-processed");
        productRepository.save(product2);
        Product product3 = new Product("tuzlu", 3, "/image-processed");
        productRepository.save(product3);

        //Run Test
        Specification<Product> specification = instanceUnderTest.getSpecification(filters, Product.class);
        Page<Product> result = productRepository.findAll(specification, pageRequest);

        //Verify
        assertEquals(2, result.getTotalElements(),
                "Specification for greater filter, should return two element");
        assertTrue(result.stream().anyMatch(product1::equals), "Should return product with price 1");
        assertTrue(result.stream().anyMatch(product2::equals), "Should return product with price 2");
    }

    @Test
    public void getSpecification_WithTwoFilters_ShouldReturnSpecificationForOneResult() {
        //Initialize
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<Filter> filters = new ArrayList(
                Arrays.asList(new Filter("price", FilterOperations.LOWER, "3"),
                        new Filter("price", FilterOperations.GREATER, "1")));

        Product product1 = new Product("sekersiz", 1, "/imageUrl");
        productRepository.save(product1);
        Product product2 = new Product("sekerli", 2, "/image-processed");
        productRepository.save(product2);
        Product product3 = new Product("tuzlu", 3, "/image-processed");
        productRepository.save(product3);

        //Run Test
        Specification<Product> specification = instanceUnderTest.getSpecification(filters, Product.class);
        Page<Product> result = productRepository.findAll(specification, pageRequest);

        //Verify
        assertEquals(1, result.getTotalElements(),
                "Specification for greater filter, should return two element");
        assertEquals(product2, result.getContent().get(0));
    }

    @Test
    public void getSpecification_WithTwoFilters_ShouldReturnSpecificationForEmptyResult() {
        //Initialize
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());
        List<Filter> filters = new ArrayList(
                Arrays.asList(new Filter("price", FilterOperations.LOWER, "2"),
                        new Filter("name", FilterOperations.EQUAL, "tuzlu")));

        Product product1 = new Product("sekersiz", 1, "/imageUrl");
        productRepository.save(product1);
        Product product2 = new Product("sekerli", 2, "/image-processed");
        productRepository.save(product2);
        Product product3 = new Product("tuzlu", 3, "/image-processed");
        productRepository.save(product3);

        //Run Test
        Specification<Product> specification = instanceUnderTest.getSpecification(filters, Product.class);
        Page<Product> result = productRepository.findAll(specification, pageRequest);

        //Verify
        assertEquals(0, result.getTotalElements(),
                "Specification for greater filter, should return two element");
    }
}


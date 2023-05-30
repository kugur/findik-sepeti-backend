package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.FilterOperations;
import com.kolip.findiksepeti.filters.specifiation.ProductSpecification;
import com.kolip.findiksepeti.filters.specifiation.SpecificationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;


import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductServiceImpl.class})
class ProductServiceImplTest {

    private ProductServiceImpl instanceUnderTest;

    @MockBean
    public ProductRepository productRepository;
    @MockBean
    public SpecificationFactory specificationFactory;

    @BeforeEach
    public void setup() {
        instanceUnderTest = new ProductServiceImpl(productRepository, specificationFactory);
    }

    @Test
    public void getProducts_WithRequestParametersAndFilters_CallRepositoryWithTheseArguments() {
        //Initialize
        ArgumentCaptor<PageRequest> repositoryPageRequestArg = ArgumentCaptor.forClass(PageRequest.class);
        ArgumentCaptor<Specification> repositorySpecifationArg = ArgumentCaptor.forClass(Specification.class);
        ArgumentCaptor<List<Filter>> getSpecificationFactoryArg = ArgumentCaptor.forClass(List.class);

        List<Filter> receivedFilters = Arrays.asList(new Filter("category", FilterOperations.EQUAL, "raw"));
        PageRequest receivedPageRequest = PageRequest.of(1, 11, Sort.by(Sort.Direction.DESC, "id,price"));
        Page<Product> repositoryResponse = new PageImpl<>(Arrays.asList(new Product("raw", 22, "/image")));
        Specification specifationFactoryResponse = new ProductSpecification();

        when(productRepository.findAll(repositorySpecifationArg.capture(), repositoryPageRequestArg.capture()))
                .thenReturn(repositoryResponse);
        when(specificationFactory.getSpecification(getSpecificationFactoryArg.capture(), eq(Product.class)))
                .thenReturn(specifationFactoryResponse);

        //Run Test
        Page<Product> response = instanceUnderTest.getProducts(receivedFilters, receivedPageRequest);

        //Verify
        verify(specificationFactory, times(1)).getSpecification(any(), any());
        verify(productRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        assertEquals(receivedPageRequest, repositoryPageRequestArg.getValue(),
                "Repository should be called with received pageRequest argument");
        assertEquals(receivedFilters, getSpecificationFactoryArg.getValue(),
                "GetSpecificationFactory should be called with received filters argument");
        assertEquals(repositorySpecifationArg.getValue(), specifationFactoryResponse,
                "Repository called with getSpecificationResponse");
        assertEquals(response, repositoryResponse, "Should return repository response");
    }
}
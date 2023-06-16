package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.FilterOperations;
import com.kolip.findiksepeti.filters.specifiation.ProductSpecification;
import com.kolip.findiksepeti.filters.specifiation.SpecificationFactory;
import com.kolip.findiksepeti.image.StorageServiceImpl;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductServiceImpl.class})
class ProductServiceImplTest {

    private ProductServiceImpl instanceUnderTest;

    @MockBean
    public ProductRepository productRepository;
    @MockBean
    public SpecificationFactory specificationFactory;
    @MockBean
    public StorageServiceImpl storageService;

    @BeforeEach
    public void setup() {
        instanceUnderTest = new ProductServiceImpl(productRepository, specificationFactory, storageService);
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

        when(productRepository.findAll(repositorySpecifationArg.capture(),
                                       repositoryPageRequestArg.capture())).thenReturn(repositoryResponse);
        when(specificationFactory.getSpecification(getSpecificationFactoryArg.capture(), eq(Product.class))).thenReturn(
                specifationFactoryResponse);

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

    @Test
    public void getProduct_WithValidId_ReturnProduct() {
        //Initialize
        long id = 12L;
        Product product = new Product("raw", BigDecimal.valueOf(123.4), "imagePath/product1.png");
        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        //Run Test
        Product result = instanceUnderTest.getProduct(id);

        //Verify Results
        assertEquals(product, result, "getProduct should return relative product by id");
        verify(productRepository, times(1)).findById(eq(id));
    }

    @Test
    public void getProduct_WithNotExistId_ReturnNull() {
        //Initialize
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Run Test
        Product result = instanceUnderTest.getProduct(11L);

        //Verify Result
        assertNull(result);
    }

    @Test
    public void getProduct_WithNotValidId_ReturnNull() {
        //Initialize
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Run Test
        Product result = instanceUnderTest.getProduct(0);

        //Verify Result
        assertNull(result);
    }

    @Test
    public void createProduct_WithValidProductModel_ReturnProduct() {
        //Initialize
        String imageUrl = "/asd_findik.jpeg";
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        MultipartFile imageFile = new MockMultipartFile("findik.jpeg", "image file data".getBytes());
        ProductModel productModel = new ProductModel("raw", BigDecimal.valueOf(11.1), null, "raw", imageFile);

        when(storageService.storeFile(any())).thenReturn(imageUrl);
        when(productRepository.save(productArgumentCaptor.capture())).thenAnswer(result -> {
            Product savedProduct = result.getArgument(0);
            savedProduct.setId(11L);
            return savedProduct;
        });

        //Run Test
        Product product = instanceUnderTest.createProduct(productModel);

        //Verify Result
        verify(storageService, times(1)).storeFile(any());
        verify(productRepository, times(1)).save(any(Product.class));
        assertEquals(productModel.getName(), product.getName(),
                     "createProdcut should save the productModel and return");
        assertEquals(imageUrl, product.getImageUrl(), "imageUrl should be equal to storageService result");
        assertNotNull(product.getId(), "product id should not be null");
    }

}

package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.config.LibraryConfiguration;
import com.kolip.findiksepeti.exceptions.InvalidArguments;
import com.kolip.findiksepeti.exceptions.StorageException;
import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.FilterOperations;
import com.kolip.findiksepeti.filters.specifiation.ProductSpecification;
import com.kolip.findiksepeti.filters.specifiation.SpecificationFactory;
import com.kolip.findiksepeti.image.StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductServiceImpl.class})
//@ContextConfiguration(classes = {LibraryConfiguration.class})
class ProductServiceImplTest {

    private ProductServiceImpl instanceUnderTest;

    @MockBean
    public ProductRepository productRepository;
    @MockBean
    public SpecificationFactory specificationFactory;
    @MockBean
    public StorageServiceImpl storageService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    public void setup() {
        instanceUnderTest =
                new ProductServiceImpl(productRepository, specificationFactory, storageService, modelMapper);
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
        ProductModel productModel = createSimpleInputs().productModel;

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

    @Test
    public void updateProduct_WithValidProductModel_ReturnProduct() {
        //Initialize
        Long productId = 123L;
        SimpleInputs simpleInputs = createSimpleInputs();
        //Product and ProductModel must have id
        simpleInputs.productModel.setId(productId);
        simpleInputs.product.setId(productId);
        //Image is not Updated. ImageFile must be null
        simpleInputs.productModel.setImageFile(null);
        when(productRepository.save(eq(simpleInputs.product))).thenReturn(simpleInputs.product);
        when(storageService.delete(any())).thenReturn(true);
        when((storageService.storeFile(any()))).thenReturn("newImageUrl");

        //Run Test
        Product result = instanceUnderTest.update(simpleInputs.productModel);

        //Assert
        assertEquals(simpleInputs.product, result, "Same product should be persist and return.");
        verify(storageService, times(0)).delete(any());
        verify(storageService, times(0)).storeFile(any());
        verify(storageService, times(0)).readFile(any());
    }

    @Test
    public void updateProduct_WithNewImageFile_ShouldStoreImage() {
        //Initialize
        Long productId = 123L;
        String newImageUrl = "/newImageUrl";
        SimpleInputs simpleInputs = createSimpleInputs();
        ArgumentCaptor<Product> beUpdatedProductCaptor = ArgumentCaptor.forClass(Product.class);

        //Product and ProductModel must have id
        simpleInputs.product.setId(productId);
        simpleInputs.productModel.setId(productId);

        //If imageFile be changed, ProductModel.imageFileChanged should return true
        simpleInputs.productModel.setImageFileChanged(true);

        when(productRepository.save(beUpdatedProductCaptor.capture())).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(storageService.delete(any())).thenReturn(true);
        when(storageService.storeFile(any())).thenReturn(newImageUrl);

        //Run Test
        Product result = instanceUnderTest.update(simpleInputs.productModel);

        //Verify Result
        verify(storageService, times(1)).storeFile(any()); // Should store the new Image file
        verify(storageService).delete(eq(simpleInputs.product.getImageUrl())); // Should delete previous image
        assertEquals(newImageUrl, result.getImageUrl(), "newImageUrl should be set on Product.");
    }

    @Test
    public void updateProduct_ImageFileIsNullAndImageFileChanged_ShouldReturnException() {
        //Initialize
        Long productId = 123L;
        SimpleInputs simpleInputs = createSimpleInputs();
        simpleInputs.productModel.setId(productId);
        simpleInputs.productModel.setImageFile(null);
        simpleInputs.productModel.setImageFileChanged(true);

        //Run Test
        assertThrows(InvalidArguments.class, () -> instanceUnderTest.update(simpleInputs.productModel));

        //Verify Result
        verify(storageService, times(0)).delete(any());
        verify(storageService, times(0)).storeFile(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    public void updateProduct_ImageFileCouldNotBeStored_ShouldReturnInternalError() {
        //Initialize
        Long productId = 123L;
        SimpleInputs simpleInputs = createSimpleInputs();
        simpleInputs.productModel.setId(productId);
        simpleInputs.productModel.setImageFileChanged(true);

        when(storageService.storeFile(any())).thenReturn(null);
        when(storageService.delete(any())).thenReturn(true);

        //Run Test
        assertThrows(StorageException.class, () -> instanceUnderTest.update(simpleInputs.productModel));

        //Validate
        verify(productRepository, times(0)).save(any());
        verify(storageService, times(0)).delete(any());
    }

    @Test
    public void deleteProduct_WithValidId_ShouldReturnTrue() {
        //Initialize
        Long productId = 123L;
        Product product = createSimpleInputs().product;
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(product));
        //Run test
        boolean result = instanceUnderTest.delete(productId);

        //Verify Result
        verify(productRepository).delete(eq(product));
        assertTrue(result, "Expected return true for successfully delete");
    }

    @Test
    public void deleteProduct_WithValidId_ShouldRemoveRelatedImageFile() {
        //Initialize
        Long productId = 123L;
        String fileUrl = "/toBeRemovedFile.png";
        Product product = createSimpleInputs().product;
        product.setImageUrl(fileUrl);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(product));

        //Run test
        boolean result = instanceUnderTest.delete(productId);

        //Verify Result
        verify(storageService).delete(eq(fileUrl));
        assertTrue(result, "Should return true for successfully delete.");
    }

    private SimpleInputs createSimpleInputs() {
        MockMultipartFile file =
                new MockMultipartFile("imageFile", "test.png", MediaType.IMAGE_JPEG_VALUE, "Image File!".getBytes());
        Product product = new Product("test", BigDecimal.valueOf(111L), "test.png", new Category(1L, "raw"), "description area");
        ProductModel productModel =
                new ProductModel(product.getName(), product.getPrice(), product.getImageUrl(), product.getCategory(),
                                 file, product.getDescription());

        return new SimpleInputs(productModel, product);
    }

    private class SimpleInputs {
        public ProductModel productModel;
        public Product product;


        public SimpleInputs(ProductModel productModel, Product product) {
            this.product = product;
            this.productModel = productModel;
        }
    }

}

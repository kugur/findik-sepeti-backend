package com.kolip.findiksepeti.products;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolip.findiksepeti.AbstractTest;
import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.filters.FilterConverter;
import com.kolip.findiksepeti.filters.FilterOperations;
import com.kolip.findiksepeti.pagination.PageRequestConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProductControllerTest extends AbstractTest {
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private ProductServiceImpl productService;


    @BeforeEach
    public void setup() {
        FilterConverter filterConverter;
        PageRequestConverter pageRequestConverter;

        filterConverter = new FilterConverter();
        pageRequestConverter = new PageRequestConverter();

        mockMvc = MockMvcBuilders.standaloneSetup(
                new ProductController(productService, filterConverter, pageRequestConverter)).build();
    }

    @Test
    void getProducts_WithoutFilters_CallProductServiceWithEmptyModel() throws Exception {
        //initialize Test
        String filterJson = "";
        Product productThatFetched = new Product("raw_nuts", 11, "/raw_nuts.jpp");
        when(productService.getProducts(any(List.class), any())).thenReturn(
                new PageImpl(List.of(productThatFetched)));

        //Run test
        mockMvc.perform(get("/products").param("filters", "")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(hasItem(productThatFetched.getName())))
                .andExpect(jsonPath("$.content[0].price").value(productThatFetched.getPrice()))
                .andExpect(jsonPath("$.content[0].category.name").value(productThatFetched.getCategory().getName()))
                .andExpect(jsonPath("$.content[*].imageUrl").value(hasItem(productThatFetched.getImageUrl())));

        //verify results
        verify(productService, times(1)).getProducts(argThat(List::isEmpty), any());
    }

    @Test
    public void getProducts_WithOutFilters_CallProductService() throws Exception {
        String productName = "findik";
        int productPrice = 11;
        String imageUrl = "/findik_image.jpg";
        String filterJson =
                "[{\"name\":\"category\",\"operation\":\"" + FilterOperations.EQUAL + "\",\"value\":\"22\"}]";

        //Setup mock items
        when(productService.getProducts(any(List.class), any())).thenReturn(
                new PageImpl(List.of(new Product(productName, productPrice, imageUrl))));

        // Run test
        mockMvc.perform(get("/products").param("filters", filterJson)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(hasItem(productName)))
                .andExpect(jsonPath("$.content[*].price").value(hasItem(productPrice)))
                .andExpect(jsonPath("$.content[*].imageUrl").value(hasItem(imageUrl)));

        // Verify results
        verify(productService, times(1)).getProducts(argThat(argument -> {
            if (argument.size() != 1) return false;

            if (argument.get(0).getName().equals("category") &&
                    argument.get(0).getOperation().name().equals(FilterOperations.EQUAL.name()) &&
                    argument.get(0).getValue().equals("22")) {
                return true;
            }
            return false;
        }), any());
    }

    @Test
    public void getProducts_WithTwoArrayFilterList_ShouldCallProductServiceWithTwoFilters() throws Exception {
        //initialize Test
        String filterJson = "[{\"name\":\"category\", \"operation\":\"EQUAL\" , \"value\":\"raw\"}," +
                "{\"name\":\"price\", \"operation\":\"GREATER\" , \"value\":\"22\"}]";
        Product productThatFetched = new Product("raw_nuts", 11, "/raw_nuts.jpp");
        when(productService.getProducts(any(List.class), any())).thenReturn(
                new PageImpl(List.of(productThatFetched)));

        //run test
        mockMvc.perform(get("/products").param("filters", filterJson)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(hasItem(productThatFetched.getName())))
                .andExpect(jsonPath("$.content[0].price").value(productThatFetched.getPrice()))
                .andExpect(jsonPath("$.content[*].imageUrl").value(hasItem(productThatFetched.getImageUrl())));

        //Verify
        verify(productService).getProducts(argThat(argument -> argument.size() == 2), any());

    }

    @Test
    public void getProducts_WithInvalidRequestparam_ShouldCallProductServiceEmptyFilter() throws Exception {
        //initialize Test
        String filterJson = "[{\"name\":\"category\", \"operation\":\"EQUAL\" , \"value\":\"raw\"}," +
                "{\"name\":\"price\", \"operation\":\"GREATER\" , \"value\":\"22\"}]";
        Product productThatFetched = new Product("raw_nuts", 11, "/raw_nuts.jpp");
        when(productService.getProducts(any(List.class), any())).thenReturn(
                new PageImpl(Arrays.asList(productThatFetched)));

        //run test
        mockMvc.perform(get("/products").param("invalidFilters", filterJson)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(hasItem(productThatFetched.getName())))
                .andExpect(jsonPath("$.content[0].price").value(productThatFetched.getPrice()))
                .andExpect(jsonPath("$.content[*].imageUrl").value(hasItem(productThatFetched.getImageUrl())));

        //Verify
        verify(productService).getProducts(argThat(argument -> argument.size() == 0), any());
    }

    @Test
    public void getProducts_WithInvalidFilterAttribute_ShouldCallProductServiceEmptyFilter() throws Exception {
        //initialize Test
        String filterJson = "[{\"InvalidName\":\"category\", \"operation\":\"EQUAL\" , \"value\":\"raw\"}," +
                "{\"name\":\"price\", \"operation\":\"GREATER\" , \"value\":\"22\"}]";
        Product productThatFetched = new Product("raw_nuts", 11, "/raw_nuts.jpp");
        when(productService.getProducts(any(List.class), any())).thenReturn(
                new PageImpl(Arrays.asList(productThatFetched)));

        //run test
        mockMvc.perform(get("/products").param("filters", filterJson)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(hasItem(productThatFetched.getName())))
                .andExpect(jsonPath("$.content[0].price").value(productThatFetched.getPrice()))
                .andExpect(jsonPath("$.content[*].imageUrl").value(hasItem(productThatFetched.getImageUrl())));

        //Verify
        verify(productService).getProducts(argThat(argument -> argument.size() == 0), any());
    }

    @Test
    public void getProducts_WithPageRequest_ShouldCallProductServiceWithPageRequest() throws Exception {
        //Initialize Test
        int pageNumber = 2;
        int pageSize = 5;
        String pageRequest = "{\"page\": " + pageNumber + ", \"size\":" + pageSize + "}";
        ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(PageRequest.class);
        when(productService.getProducts(any(List.class), pageRequestArgument.capture())).thenReturn(
                new PageImpl<>(new ArrayList<>()));

        //Run test
        mockMvc.perform(get("/products").param("pageInfo", pageRequest)).andExpect(status().isOk());

        //Verify
        assertNotNull(pageRequestArgument.getValue());
        assertEquals(1, pageRequestArgument.getAllValues().size());
        assertEquals(pageNumber, pageRequestArgument.getAllValues().get(0).getPageNumber());
        assertEquals(pageSize, pageRequestArgument.getAllValues().get(0).getPageSize());
    }

    @Test
    public void getProduct_WithProductId_ShouldReturnProduct() throws Exception {
        //Initialize Test
        long productId = 11L;
        String productName = "raw";
        BigDecimal productPrice = BigDecimal.valueOf(31);

        Product productThatBeFetched = new Product("raw", productPrice, "imagePath/");
        productThatBeFetched.setId(productId);

        when(productService.getProduct(eq(productId))).thenReturn(productThatBeFetched);

        // Run Test
        mockMvc.perform(get("/products/" + productId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productName)).andExpect(jsonPath("$.price").value(productPrice));
    }

    @Test
    public void getProduct_WithNotExistProductId_ShouldReturn404() throws Exception {
        //Initialize Test
        when(productService.getProduct(any(Long.class))).thenReturn(null);

        //Run Test
        mockMvc.perform(get("/product/33")).andExpect(status().isNotFound());
    }

    @Test
    public void getProduct_WithNotValidProductId_ShouldReturn400() throws Exception {
        //Initialize Test
        when(productService.getProduct(any(Long.class))).thenReturn(null);

        //Run Test
        mockMvc.perform(get("/products/anana")).andExpect(status().isBadRequest());
    }

    @Test
    public void createProduct_WithValidValues_ShouldReturnResult() throws Exception {
        //Initialize
        MockMultipartFile file =
                new MockMultipartFile("imageFile", "test.png", MediaType.IMAGE_JPEG_VALUE, "Image File!".getBytes());
        SimpleInput simpleInput = createSimpleInput();
        Product product = simpleInput.product;
        ProductModel productModel = simpleInput.productModel;

        //Initialize
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", product.getName());
        formData.add("price", product.getPrice().toPlainString());
        formData.add("categoryId", "" + product.getCategory().getId());
        formData.add("description", product.getDescription());

        when(productService.createProduct(argThat(receivedProduct -> receivedProduct.getImageFile() != null &&
                productModel.getName().equals(receivedProduct.getName())))).thenReturn(product);

        // Run Test
        mockMvc.perform(MockMvcRequestBuilders.multipart("/products").file(file).params(formData))
                .andExpect(status().isOk()).andExpect(jsonPath("$.price").value(productModel.getPrice()));
    }

    @Test
    public void updateProduct_WithValidValues_ShouldReturnResult() throws Exception {
        //Initialize
        SimpleInput simpleInput = createSimpleInput();
        when(productService.update(eq(simpleInput.productModel))).thenReturn(simpleInput.product);

        //Run test
        mockMvc.perform(multipart(HttpMethod.PUT, "/products", simpleInput.formData).file(simpleInput.file))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteProduct_WithValidId_ShouldReturnResult() throws Exception {
        //Initialize
        Long productId = 12L;
        when(productService.delete(any())).thenReturn(true);

        //Run Test
        mockMvc.perform(delete("/products/" + productId)).andExpect(status().isOk());

        //Verify Result
        verify(productService, times(1)).delete(eq(productId));
    }

    private SimpleInput createSimpleInput() throws JsonProcessingException {
        MockMultipartFile file =
                new MockMultipartFile("imageFile", "test.png", MediaType.IMAGE_JPEG_VALUE, "Image File!".getBytes());
        Product product = new Product("test", BigDecimal.valueOf(111L), "test.png", new Category(1L, "raw"), "description area");
        ProductModel productModel =
                new ProductModel(product.getName(), product.getPrice(), product.getImageUrl(), product.getCategory(),
                                 file, product.getDescription());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", product.getName());
        formData.add("price", product.getPrice().toPlainString());
        formData.add("categoryId", "" + product.getCategory().getId());

        return new SimpleInput(productModel, product, file, formData);
    }

    private class SimpleInput {
        public ProductModel productModel;
        public Product product;
        public MockMultipartFile file;
        public MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        public SimpleInput(ProductModel productModel, Product product, MockMultipartFile file,
                           MultiValueMap<String, String> formData) {
            this.productModel = productModel;
            this.product = product;
            this.file = file;
            this.formData = formData;
        }

    }
}
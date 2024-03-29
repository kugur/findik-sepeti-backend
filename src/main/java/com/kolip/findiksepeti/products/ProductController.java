package com.kolip.findiksepeti.products;

import com.kolip.findiksepeti.filters.FilterConverter;
import com.kolip.findiksepeti.pagination.PageRequestConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private ProductService productService;
    private FilterConverter filterConverter;
    private PageRequestConverter pageRequestConverter;

    public ProductController(ProductService productService, FilterConverter filterConverter,
                             PageRequestConverter pageRequestConverter) {
        this.productService = productService;
        this.filterConverter = filterConverter;
        this.pageRequestConverter = pageRequestConverter;
        logger.info("ProductController has been created");
    }

    @RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Product> getProduct(@PathVariable("productId") Long productId) {
        Product product = productService.getProduct(productId);
        return product == null ? ResponseEntity.status(HttpStatusCode.valueOf(404)).build() :
                ResponseEntity.ok(product);
    }

    @GetMapping("/products")
    public Page<Product> getProducts(@RequestParam(value = "filters", defaultValue = "") String filtersJson,
                                     @RequestParam(value = "pageInfo", defaultValue = "{page=0,size=10, sort=ASC,id}")
                                     String paginationJson) {
        return productService.getProducts(filterConverter.convert(filtersJson),
                                                          pageRequestConverter.convert(paginationJson));
    }

    @PostMapping("/products")
    @ResponseBody
    public Product createProduct(@ModelAttribute ProductModel product) {
        return productService.createProduct(product);
    }

    @PutMapping("/products")
    public Product updateProduct(@ModelAttribute ProductModel productModel) {
        return productService.update(productModel);
    }

    @DeleteMapping("/products/{productId}")
    public boolean deleteProduct(@PathVariable("productId") Long productId) {
        return productService.delete(productId);
    }
}

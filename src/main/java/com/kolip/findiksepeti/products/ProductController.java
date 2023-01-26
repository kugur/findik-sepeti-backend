package com.kolip.findiksepeti.products;

import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @GetMapping("/products")
//    @CrossOrigin(origins = "http://localhost:3000")
    public Product getProduct() {

        return new Product("findik", 11);
    }

    @PostMapping("/products")
    @ResponseBody
    public Product createProduct(@RequestBody Product product) {
        return product;
    }
}

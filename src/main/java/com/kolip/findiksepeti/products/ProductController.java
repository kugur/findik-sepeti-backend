package com.kolip.findiksepeti.products;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ProductController {

    @GetMapping("/products")
//    @CrossOrigin(origins = "http://localhost:3000")
    public Product getProduct() {

        return new Product("findik", 11);
    }
}

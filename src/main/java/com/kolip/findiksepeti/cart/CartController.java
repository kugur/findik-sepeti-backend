package com.kolip.findiksepeti.cart;

import com.kolip.findiksepeti.products.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CartController {

    @GetMapping("/carts")
    public List<Product> getProducts(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("deneme", 1234);
        return new ArrayList<>();
    }


    @GetMapping("/cartsResult")
    public String verifyResult(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer resultDeneme = (Integer) session.getAttribute("deneme");
        return Integer.toString(resultDeneme);
    }

}

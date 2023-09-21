package com.kolip.findiksepeti.cart;

import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.session.SessionStoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CartController {
    private Logger logger = LoggerFactory.getLogger(CartController.class);
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/cart")
    public ResponseEntity<Boolean> setCartItem(@RequestBody CartItem cartItem) {
        logger.info("Cart is added request received. CartItem: {}", cartItem);
        boolean cartItemAdded = cartService.addItem(cartItem);
        return ResponseEntity.status(HttpStatus.OK).body(cartItemAdded);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Boolean> deleteCartItem(@RequestParam(value = "productId") Long cartItemId) {
        boolean successfullyDeleted = cartService.deleteItem(cartItemId);
        return ResponseEntity.ok(successfullyDeleted);
    }

    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getCart() {
        List<CartItem> storedItems = cartService.getCartItems();
        return ResponseEntity.ok(storedItems);
    }
}

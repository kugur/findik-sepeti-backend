package com.kolip.findiksepeti.cart;

import com.kolip.findiksepeti.products.Product;

import java.util.List;

public interface CartService {
    boolean addItem(CartItem product);

    boolean deleteItem(Long productId);

    List<CartItem> getCartItems();

    void clearCartItems();
}

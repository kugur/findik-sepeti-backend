package com.kolip.findiksepeti.cart;

import com.kolip.findiksepeti.products.Product;

public interface CartService {
    boolean addProduct(Product product);

    boolean deleteProduct(Product product);
}

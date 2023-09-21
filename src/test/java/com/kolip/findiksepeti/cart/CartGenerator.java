package com.kolip.findiksepeti.cart;

import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.products.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartGenerator {
    public static CartItem generateCartItem() {
        Category category = new Category(1L, "raw");
        Product product = new Product("bahceden", BigDecimal.valueOf(10), "/imageUrl.jpg", category, "desc");
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        category.setProducts(products);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        return cartItem;
    }
}

package com.kolip.findiksepeti.cart;

import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.session.SessionStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final SessionStoreService<CartItem> sessionStoreService;

    public CartServiceImpl(SessionStoreService<CartItem> sessionStoreService) {
        this.sessionStoreService = sessionStoreService;
    }

    @Override
    public boolean addItem(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null) {
            logger.warn("Invalid request for cartItem: {} ", cartItem);
            return false;
        }
        sessionStoreService.store(cartItem.getId(), cartItem);
        return true;
    }

    @Override
    public boolean deleteItem(Long cartItemId) {
        if (cartItemId == null || cartItemId <= 0L) {
            return false;
        }
        sessionStoreService.delete(cartItemId);
        return true;
    }

    @Override
    public List<CartItem> getCartItems() {
        return sessionStoreService.getAll();
    }
}

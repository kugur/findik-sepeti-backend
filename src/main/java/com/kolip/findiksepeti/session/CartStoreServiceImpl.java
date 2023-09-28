package com.kolip.findiksepeti.session;

import com.kolip.findiksepeti.cart.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SessionScope
@Service
public class CartStoreServiceImpl implements SessionStoreService<CartItem> {
    private Logger logger = LoggerFactory.getLogger(CartStoreServiceImpl.class);

    private final HashMap<Long, CartItem> storedMap = new HashMap();

    @Override
    public void store(Long key, CartItem value) {
        logger.debug("Store key {} , value: {}", key, value);
        storedMap.put(key, value);
    }

    @Override
    public CartItem getValue(Long key) {
        return storedMap.get(key);
    }

    @Override
    public void delete(Long key) {
        storedMap.remove(key);
    }

    @Override
    public List<CartItem> getAll() {
        return new ArrayList<>(storedMap.values());
    }

    @Override
    public void deleteAll() {
        storedMap.clear();
    }
}

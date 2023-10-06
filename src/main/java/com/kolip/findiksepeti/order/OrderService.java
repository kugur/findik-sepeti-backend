package com.kolip.findiksepeti.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface OrderService {
    OrderStatus createOrder(Order order);

    Page<Order> getProducts(PageRequest pageInfo);
}

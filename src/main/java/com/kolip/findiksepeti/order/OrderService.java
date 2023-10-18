package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.filters.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderService {
    OrderStatus createOrder(Order order);

    Page<Order> getOrders(PageRequest pageInfo);

    Page<Order> getAllOrders(PageRequest pageRequest, List<Filter> capture);

    boolean updateStatus(Long orderId, OrderStatus status);
}

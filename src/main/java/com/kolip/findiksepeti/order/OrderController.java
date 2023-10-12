package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.pagination.PageRequestConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final PageRequestConverter pageRequestConverter;

    public OrderController(OrderService orderService, PageRequestConverter pageRequestConverter) {
        this.orderService = orderService;
        this.pageRequestConverter = pageRequestConverter;
    }

    @PostMapping("/order")
    public ResponseEntity<OrderStatus> createOrder(@RequestBody Order order) {
        OrderStatus response = orderService.createOrder(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order")
    public Page<Order> getOrders(
            @RequestParam(value = "pageInfo", defaultValue = "{\"page\":0,\"size\":10, \"sort\":\"DESC,id\"}")
            String paginationJson) {
        PageRequest pageRequest = pageRequestConverter.convert(paginationJson);
        return orderService.getOrders(pageRequest);
    }
}

package com.kolip.findiksepeti.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<OrderStatus> createOrder(@RequestBody Order order) {
        OrderStatus response = orderService.createOrder(order);
        return ResponseEntity.ok(response);
    }

}

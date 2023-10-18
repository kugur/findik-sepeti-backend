package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.FilterConverter;
import com.kolip.findiksepeti.pagination.PageRequestConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final PageRequestConverter pageRequestConverter;
    private final FilterConverter filterConverter;

    public OrderController(OrderService orderService, PageRequestConverter pageRequestConverter,
                           FilterConverter filterConverter) {
        this.orderService = orderService;
        this.pageRequestConverter = pageRequestConverter;
        this.filterConverter = filterConverter;
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

    @GetMapping("/orderAll")
    public Page<Order> getAllOrders(@RequestParam(value = "pageInfo",
            defaultValue = "{\"page\":0,\"size\":10, \"sort\":\"DESC,id\"}") String paginationJson,
                                    @RequestParam(value = "filters", defaultValue = "") String filterJson) {
        PageRequest pageRequest = pageRequestConverter.convert(paginationJson);
        List<Filter> filters = filterConverter.convert(filterJson);
        return orderService.getAllOrders(pageRequest, filters);
    }

    @PutMapping("/orderStatus/{id}")
    public boolean updateStatus(@PathVariable("id") Long id, @RequestParam(value = "status") String status) {

        return orderService.updateStatus(id, OrderStatus.valueOf(status));
    }
}

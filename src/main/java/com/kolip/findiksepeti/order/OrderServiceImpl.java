package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.cart.CartItem;
import com.kolip.findiksepeti.cart.CartService;
import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.specifiation.SpecificationFactory;
import com.kolip.findiksepeti.payment.PaymentService;
import com.kolip.findiksepeti.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ugur.kolip on 27/09/2023.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final SpecificationFactory specificationFactory;

    public OrderServiceImpl(PaymentService paymentService, OrderRepository orderRepository, CartService cartService,
                            UserService userService, SpecificationFactory specificationFactory) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.specificationFactory = specificationFactory;
    }

    @Override
    public OrderStatus createOrder(Order order) {
        //TODO(ugur) Product fiyati degisp degismedigi de kontrol edilmesi gerekir !!
        if (!validate(order)) {
            cartService.clearCartItems();
            return OrderStatus.INVALID_ORDER;
        }
        boolean successfullyPaid = paymentService.pay(order.getPayment());
        if (!successfullyPaid) {
            return OrderStatus.FAILED_PAYMENT;
        }

        //Set current User
        order.setUser(userService.getCurrentUser());

        Order createdOrder = orderRepository.save(order);
        cartService.clearCartItems();
        return createdOrder.getStatus();
    }

    @Override
    public Page<Order> getOrders(PageRequest pageInfo) {
        return orderRepository.findByUserId(pageInfo, userService.getCurrentUser().getId());
    }

    @Override
    public Page<Order> getAllOrders(PageRequest pageRequest, List<Filter> filters) {
        Specification<Order> orderSpec = specificationFactory.getSpecification(filters, Order.class);
        return orderRepository.findAll(orderSpec, pageRequest);
    }

    @Override
    public boolean updateStatus(Long orderId, OrderStatus status) {
        orderRepository.updateStatusById(status, orderId);
        return true;
    }

    private boolean validate(Order order) {
        if (order == null || order.getOrderItems().isEmpty()) {
            logger.warn("createOrder has been called with empty orderItemList {} ", order);
            return false;
        }

        boolean anyNullProductId = order.getOrderItems().stream()
                .anyMatch(orderItem -> orderItem.getProduct() == null || orderItem.getProduct().getId() == 0);

        if (anyNullProductId) {
            logger.warn("create Order has been called with null product Id, {} ", order);
            return false;
        }

        if (!isMatchWithCartsOnSession(order)) {
            logger.warn("OrderItems that sent, do not match with cartItems that stored on session. orderItems: {} ," +
                    " cartItems: {} ", order.getOrderItems(), cartService.getCartItems());
            return false;
        }

        return true;
    }

    private boolean isMatchWithCartsOnSession(Order order) {
        if (order.getOrderItems().size() != cartService.getCartItems().size()) {
            return false;
        }

        return order.getOrderItems().stream().allMatch(orderItem -> cartService.getCartItems().stream()
                .anyMatch(cartItem -> isEqualToCart(orderItem, cartItem)));
    }

    private boolean isEqualToCart(OrderItem orderItem, CartItem cartItem) {
        return orderItem.getProduct().equals(cartItem.getProduct()) &&
                orderItem.getQuantity() == cartItem.getQuantity();
    }
}

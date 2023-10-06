package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.cart.CartItem;
import com.kolip.findiksepeti.cart.CartService;
import com.kolip.findiksepeti.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Created by ugur.kolip on 27/09/2023.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderServiceImpl(PaymentService paymentService, OrderRepository orderRepository, CartService cartService) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
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

        Order createdOrder = orderRepository.save(order);
        cartService.clearCartItems();
        return createdOrder.getStatus();
    }

    @Override
    public Page<Order> getProducts(PageRequest pageInfo) {
        return orderRepository.findAll(pageInfo);
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

package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.cart.CartItem;
import com.kolip.findiksepeti.cart.CartService;
import com.kolip.findiksepeti.payment.OrderRepository;
import com.kolip.findiksepeti.payment.PaymentService;
import com.kolip.findiksepeti.shipping.ShippingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {OrderServiceImpl.class})
public class OrderServiceImplTest {
    private OrderService instanceUnderTest;
    @MockBean
    ShippingService shippingService;
    @MockBean
    PaymentService paymentService;
    @MockBean
    OrderRepository orderRepository;
    @MockBean
    CartService cartService;

    @BeforeEach
    public void setup() {
        instanceUnderTest = new OrderServiceImpl(shippingService, paymentService, orderRepository, cartService);
    }

    @Test
    public void createOrder_WithValidParameters_CreateOrder() {
        //Initialize
        Order receivedOrder = OrderGenerator.createOder(2);
        validMockResponse(receivedOrder);

        //Run Test
        OrderStatus orderStatus = instanceUnderTest.createOrder(receivedOrder);

        //Verify Result
        assertEquals(OrderStatus.ORDER_CREATED, orderStatus);
        verify(paymentService).pay(any());
        verify(orderRepository).save(eq(receivedOrder));
    }

    @Test
    public void createOrder_PaymentFailed_ShouldNotPersist() {
        //Initialize
        Order receivedOrder = OrderGenerator.createOder(2);
        mockResponse(receivedOrder, false);

        //Run Test
        OrderStatus orderStatus = instanceUnderTest.createOrder(receivedOrder);

        //Verify Result
        assertEquals(OrderStatus.FAILED_PAYMENT, orderStatus);
        verify(paymentService).pay(any());
        verify(orderRepository, times(0)).save(any());
    }

    @Test
    public void createOrder_PaymentSuccessfully_ShouldRemoveCartsFromSession() {
        //Initialize
        int cartItemCount = 2;
        Order receivedOrder = OrderGenerator.createOder(cartItemCount);
        validMockResponse(receivedOrder);

        //Run Test
        OrderStatus status = instanceUnderTest.createOrder(receivedOrder);

        //Verify result
        assertEquals(OrderStatus.ORDER_CREATED, status);
        verify(paymentService).pay(any());
        verify(orderRepository).save(any());
        verify(cartService, times(cartItemCount)).deleteItem(any());
    }

    @Test
    public void createOrder_WithNullProductId_ShouldNotCreateOrder() {
        //Initialize
        int cartItemCount = 1;
        Order receivedOrder = OrderGenerator.createOder(cartItemCount);
        receivedOrder.getOrderItems().get(0).getProduct().setId(0L);
        validMockResponse(receivedOrder);

        //Run Test
        OrderStatus status = instanceUnderTest.createOrder(receivedOrder);

        //Verify Result
        assertEquals(OrderStatus.INVALID_ORDER, status);
        verify(paymentService, times(0)).pay(any());
        verify(orderRepository, times(0)).save(any());
        verify(cartService).clearCartItems();
    }

    @Test
    public void createOrder_NotMatchWithSessionCartList_ShouldNotCreateOrder() {
        //Initialize
        int cartItemCount = 2;
        Order receivedOrder = OrderGenerator.createOder(cartItemCount);
        validMockResponse(receivedOrder);
        CartItem cartItem1 = new CartItem();
        cartItem1.setProduct(receivedOrder.getOrderItems().get(0).getProduct());
        cartItem1.setQuantity(receivedOrder.getOrderItems().get(0).getQuantity());
        when(cartService.getCartItems()).thenReturn(List.of(cartItem1));

        //Run Test
        OrderStatus status = instanceUnderTest.createOrder(receivedOrder);

        //Verify Result
        assertEquals(OrderStatus.INVALID_ORDER, status);
        verify(paymentService, times(0)).pay(any());
        verify(orderRepository, times(0)).save(any());
        verify(cartService).clearCartItems();
    }

    private void mockResponse(Order receivedOrder, boolean paymentResult) {
        when(paymentService.pay(any())).thenReturn(paymentResult);
        when(orderRepository.save(receivedOrder)).thenAnswer(invocation -> {
            Order toBeSavedOrder = invocation.getArgument(0);
            toBeSavedOrder.setId(11L);
            return toBeSavedOrder;
        });
        when(cartService.deleteItem(any())).thenReturn(true);
        when(cartService.getCartItems()).thenReturn(convertOrderItems(receivedOrder.getOrderItems()));
    }

    private void validMockResponse(Order receivedOrder) {
        mockResponse(receivedOrder, true);
    }

    private List<CartItem> convertOrderItems(List<OrderItem> orderItems) {
        return orderItems.stream().map(orderItem -> {
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(orderItem.getQuantity());
            cartItem.setProduct(orderItem.getProduct());
            return cartItem;
        }).collect(Collectors.toList());
    }
}
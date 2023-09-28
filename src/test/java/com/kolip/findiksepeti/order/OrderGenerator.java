package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.payment.Payment;
import com.kolip.findiksepeti.products.ProductGenerator;
import com.kolip.findiksepeti.shipping.Shipping;

import java.util.Arrays;

public class OrderGenerator {

    public static Order createOder(int orderItemCount) {
        Order order = new Order();

        Payment payment = new Payment("1234");
        order.setPayment(payment);

        Shipping shipping = new Shipping();
        shipping.setAddress("teslimat adresi vs..");
        order.setShipping(shipping);

        OrderItem orderItem1 = new OrderItem(ProductGenerator.createProduct(1L), 2);
        OrderItem orderItem2 = new OrderItem(ProductGenerator.createProduct(2L), 1);
        order.setOrderItems(Arrays.asList(orderItem1, orderItem2));

        return order;
    }
}

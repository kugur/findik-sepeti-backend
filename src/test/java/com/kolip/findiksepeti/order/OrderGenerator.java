package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.payment.Payment;
import com.kolip.findiksepeti.products.ProductGenerator;
import com.kolip.findiksepeti.shipping.Shipping;

import java.util.ArrayList;
import java.util.List;

public class OrderGenerator {

    public static Order createOder(int orderItemCount) {
        Order order = new Order();

        Payment payment = new Payment("1234");
        order.setPayment(payment);

        Shipping shipping = new Shipping();
        shipping.setAddress("teslimat adresi vs..");
        order.setShipping(shipping);

        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < orderItemCount; i++) {
            orderItems.add(new OrderItem(ProductGenerator.createProduct(orderItemCount + 1L), 2));
        }
        order.setOrderItems(orderItems);

        return order;
    }
}

package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.common.AbstractEntity;
import com.kolip.findiksepeti.payment.Payment;
import com.kolip.findiksepeti.shipping.Shipping;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Order extends AbstractEntity {
    @Transient
    private Payment payment;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status = OrderStatus.ORDER_CREATED;
    @OneToOne(cascade = CascadeType.ALL)
    private Shipping shipping;

    public Order() {
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    @Override
    public String toString() {
        return "Order{" + "payment=" + payment + ", orderItems=" + orderItems + ", shipping=" + shipping + '}';
    }


}

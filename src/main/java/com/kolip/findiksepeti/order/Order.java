package com.kolip.findiksepeti.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolip.findiksepeti.common.AbstractEntity;
import com.kolip.findiksepeti.payment.Payment;
import com.kolip.findiksepeti.shipping.Shipping;
import com.kolip.findiksepeti.user.CustomUser;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sales_order")
@DynamicUpdate
//@NamedEntityGraph(name = "order.findAll",
//        attributeNodes = {@NamedAttributeNode("shipping"), @NamedAttributeNode("orderItems")},
//        includeAllAttributes = true)
public class Order extends AbstractEntity {

    @Transient
    private Payment payment;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status = OrderStatus.ORDER_CREATED;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;
    private final LocalDateTime createdDate = LocalDateTime.now();
    @ManyToOne
    @JsonIgnore
    private CustomUser user;
    @Transient
    private long total;

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

    public CustomUser getUser() {
        return user;
    }

    public void setUser(CustomUser user) {
        this.user = user;
    }

    public BigDecimal getTotal() {
        return orderItems.stream().map(orderItem -> orderItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return "Order{" + "payment=" + payment + ", orderItems=" + orderItems + ", shipping=" + shipping + '}';
    }


}

package com.kolip.findiksepeti.payment;

import com.kolip.findiksepeti.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

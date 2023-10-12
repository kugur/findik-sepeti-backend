package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.order.Order;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"shipping"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"shipping"})
    Page<Order> findByUserId(Pageable pageable, Long userId);
}

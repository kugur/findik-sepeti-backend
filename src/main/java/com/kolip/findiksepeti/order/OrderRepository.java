package com.kolip.findiksepeti.order;

import com.kolip.findiksepeti.order.Order;
import jakarta.persistence.QueryHint;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Transactional
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"shipping"})
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"shipping"})
    Page<Order> findByUserId(Pageable pageable, Long userId);

    @Modifying
    @Query("Update Order o SET o.status = :status WHERE o.id = :id")
    void updateStatusById(@Param("status") OrderStatus status, @Param("id") Long id);

}

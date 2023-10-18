package com.kolip.findiksepeti.filters.specifiation;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.order.Order;
import com.kolip.findiksepeti.order.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class OrderSpecification extends AbstractSpecification<Order> {

    public OrderSpecification(Filter filter) {
        super(filter);
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (filter == null) return  null;

        if (filter.getName().equals("status")) {
            return criteriaBuilder.equal(root.get(filter.getName()), OrderStatus.valueOf(filter.getValue()));
        }

        return super.toPredicate(root, query, criteriaBuilder);
    }
}

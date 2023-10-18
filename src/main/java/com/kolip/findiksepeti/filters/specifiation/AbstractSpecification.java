package com.kolip.findiksepeti.filters.specifiation;

import com.kolip.findiksepeti.filters.Filter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class AbstractSpecification<T> implements Specification<T> {
    protected Filter filter;

    public AbstractSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        switch (filter.getOperation()) {
            case EQUAL:
                return criteriaBuilder.equal(root.get(filter.getName()), filter.getValue());
            case LOWER:
                return criteriaBuilder.lessThan(root.get(filter.getName()), filter.getValue());
            case GREATER:
                return criteriaBuilder.greaterThan(root.get(filter.getName()), filter.getValue());
            default:
                return null;
        }
    }
}

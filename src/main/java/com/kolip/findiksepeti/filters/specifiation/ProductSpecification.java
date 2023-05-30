package com.kolip.findiksepeti.filters.specifiation;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.products.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification implements Specification<Product> {
    private Filter filter;

    public ProductSpecification() {
    }

    public ProductSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

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

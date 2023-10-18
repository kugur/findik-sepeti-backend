package com.kolip.findiksepeti.filters.specifiation;

import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.products.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification extends AbstractSpecification<Product> {

    public ProductSpecification() {
        super(new Filter());
    }

    public ProductSpecification(Filter filter) {
        super(filter);
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (filter == null || filter.getName() == null) return null;

        if (Product.class == query.getResultType() && !root.getJoins().stream().anyMatch(productJoin -> productJoin.toString().equals("category"))) {
            root.fetch("category");
        }

        if ("category".equals(filter.getName())) {
            return criteriaBuilder.equal(root.get("category").get("name"), filter.getValue());
        }

        return super.toPredicate(root, query, criteriaBuilder);
    }
}

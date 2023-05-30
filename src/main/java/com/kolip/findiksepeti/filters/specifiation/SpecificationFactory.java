package com.kolip.findiksepeti.filters.specifiation;

import com.kolip.findiksepeti.filters.Filter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecificationFactory {

    public <T> Specification<T> getSpecification(List<Filter> filterList, Class<T> specificationElement) {
        Specification<T> combinedSpecification = null;
        Specification<T> currentSpecification;

        for (Filter filter : filterList) {
            currentSpecification = getBuildSpecification(filter, specificationElement);
            if (combinedSpecification == null && currentSpecification != null) {
                combinedSpecification = currentSpecification;
            } else if (currentSpecification != null) {
                combinedSpecification = combinedSpecification.and(currentSpecification);
            }
        }
        return combinedSpecification;
    }

    private <T> Specification<T> getBuildSpecification(Filter filter, Class<T> specificationElement) {
        Specification result;
        switch (specificationElement.getSimpleName()) {
            case "Product":
                result = new ProductSpecification(filter);
                return result;
            case "User":
                return null;
        }
        return null;
    }
}

package com.zarnab.panel.common.search;

import com.zarnab.panel.core.dto.req.FilterRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public final class SpecificationBuilder {

    private SpecificationBuilder() {
    }

    public static <T> Specification<T> build(List<FilterRequest> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        return filters.stream()
                .<Specification<T>>map(GenericSpecification::new)
                .reduce(Specification::and)
                .orElse(null);
    }
}

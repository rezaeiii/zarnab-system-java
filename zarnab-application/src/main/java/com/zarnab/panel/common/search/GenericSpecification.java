package com.zarnab.panel.common.search;

import com.zarnab.panel.core.dto.req.FilterRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

public class GenericSpecification<T> implements Specification<T> {

    private final FilterRequest filter;

    public GenericSpecification(FilterRequest filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return switch (filter.getOperator()) {
            case EQUAL -> builder.equal(root.get(filter.getField()), filter.getValue());
            case NOT_EQUAL -> builder.notEqual(root.get(filter.getField()), filter.getValue());
            case LIKE ->
                    builder.like(builder.lower(root.get(filter.getField())), "%" + filter.getValue().toLowerCase() + "%");
            case GREATER_THAN -> builder.greaterThan(root.get(filter.getField()), filter.getValue());
            case LESS_THAN -> builder.lessThan(root.get(filter.getField()), filter.getValue());
            case GREATER_THAN_OR_EQUAL -> builder.greaterThanOrEqualTo(root.get(filter.getField()), filter.getValue());
            case LESS_THAN_OR_EQUAL -> builder.lessThanOrEqualTo(root.get(filter.getField()), filter.getValue());
            case IS_NULL -> builder.isNull(root.get(filter.getField()));
            case IS_NOT_NULL -> builder.isNotNull(root.get(filter.getField()));
            case BETWEEN -> builder.between(root.get(filter.getField()), filter.getValue(), filter.getValueTo());
            case IN -> {
                final List<String> values = Arrays.asList(filter.getValue().split(","));
                yield root.get(filter.getField()).in(values);
            }
        };
    }
}

package com.zarnab.panel.common.search;

import com.zarnab.panel.core.dto.req.FilterRequest;
import jakarta.persistence.criteria.*;
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
        Path<?> path = getPath(root, filter.getField());

        return switch (filter.getOperator()) {
            case EQUAL -> builder.equal(path, filter.getValue());
            case NOT_EQUAL -> builder.notEqual(path, filter.getValue());
            case LIKE -> builder.like(builder.lower(path.as(String.class)), "%" + filter.getValue().toLowerCase() + "%");
            case GREATER_THAN -> builder.greaterThan(path.as(String.class), filter.getValue());
            case LESS_THAN -> builder.lessThan(path.as(String.class), filter.getValue());
            case GREATER_THAN_OR_EQUAL -> builder.greaterThanOrEqualTo(path.as(String.class), filter.getValue());
            case LESS_THAN_OR_EQUAL -> builder.lessThanOrEqualTo(path.as(String.class), filter.getValue());
            case IS_NULL -> builder.isNull(path);
            case IS_NOT_NULL -> builder.isNotNull(path);
            case BETWEEN -> builder.between(path.as(String.class), filter.getValue(), filter.getValueTo());
            case IN -> {
                final List<String> values = Arrays.asList(filter.getValue().split(","));
                yield path.in(values);
            }
        };
    }

    private Path<?> getPath(From<?, ?> root, String fieldName) {
        if (fieldName.contains(".")) {
            String[] parts = fieldName.split("\\.");
            From<?, ?> join = root;
            for (int i = 0; i < parts.length - 1; i++) {
                join = join.join(parts[i], JoinType.LEFT);
            }
            return join.get(parts[parts.length - 1]);
        } else {
            return root.get(fieldName);
        }
    }
}

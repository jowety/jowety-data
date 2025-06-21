package com.jowety.data.query.jpa;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Filter;

public class GreaterThanOrEqualBuilder  extends PredicateBuilderBase {

	public GreaterThanOrEqualBuilder(boolean negative) {
		super(negative);
	}

	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {

		Expression left = buildExpression(cb, from, filter.getLeftSide(), pathMap);
		Expression right = buildExpression(cb, from, filter.getRightSide(), pathMap);

		Predicate out = isNegative(filter.isNegative())?
				cb.not(cb.greaterThanOrEqualTo(left, right)):
					cb.greaterThanOrEqualTo(left, right);
				return out;
	}

}

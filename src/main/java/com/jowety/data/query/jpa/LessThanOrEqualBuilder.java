package com.jowety.data.query.jpa;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Exp.ExpType;
import com.jowety.data.query.Filter;

public class LessThanOrEqualBuilder  extends PredicateBuilderBase {

	public LessThanOrEqualBuilder(boolean negative) {
		super(negative);
	}

	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {

		if(filter.getRightSide().getType()== ExpType.LITERAL &&
				!Comparable.class.isAssignableFrom(filter.getRightSide().getValue().getClass()))
			throw new RuntimeException(
					"Filter values for LESS_THAN_OR_EQUAL MatchMode must be of type java.lang.Comparable");

		Expression left = buildExpression(cb, from, filter.getLeftSide(), pathMap);
		Expression right = buildExpression(cb, from, filter.getRightSide(), pathMap);

		Predicate out = isNegative(filter.isNegative())?
				cb.not(cb.lessThanOrEqualTo(left, right)):
					cb.lessThanOrEqualTo(left, right);
				return out;
	}

}

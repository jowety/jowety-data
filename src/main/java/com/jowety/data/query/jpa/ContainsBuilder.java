package com.jowety.data.query.jpa;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Exp.ExpType;
import com.jowety.data.query.Filter;

public class ContainsBuilder  extends PredicateBuilderBase {

	public ContainsBuilder(boolean negative) {
		super(negative);
	}

	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {

		if(filter.getRightSide().getType()== ExpType.LITERAL &&
				!String.class.isAssignableFrom(filter.getRightSide().getValue().getClass()))
			throw new RuntimeException(
					"Filter values for LIKE MatchMode must be of type java.lang.String");

		Expression left = buildExpression(cb, from, filter.getLeftSide(), pathMap);
		Expression right = buildExpression(cb, from, filter.getRightSide(), pathMap);
		right = cb.concat("%", right);
		right = cb.concat(right, "%");

		Predicate out = isNegative(filter.isNegative())?
				cb.notLike(left, right):
					cb.like(left, right);

				return out;
	}

}

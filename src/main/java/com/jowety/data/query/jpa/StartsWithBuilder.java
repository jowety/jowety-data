package com.jowety.data.query.jpa;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Exp.ExpType;
import com.jowety.data.query.Filter;

public class StartsWithBuilder  extends PredicateBuilderBase {

	public StartsWithBuilder(boolean negative) {
		super(negative);
	}

	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {

		if(filter.getRightSide().getType()== ExpType.LITERAL &&
				!String.class.isAssignableFrom(filter.getRightSide().getValue().getClass()))
			throw new RuntimeException(
					"Filter values for STARTSWITH MatchMode must be of type java.lang.String");

		Expression left = buildExpression(cb, from, filter.getLeftSide(), pathMap);
		Expression right = buildExpression(cb, from, filter.getRightSide(), pathMap);
		right = cb.concat(right, "%");
		Predicate out = isNegative(filter.isNegative())?
				cb.not(cb.like(left, right)):
					cb.like(left, right);

				return out;
	}


}

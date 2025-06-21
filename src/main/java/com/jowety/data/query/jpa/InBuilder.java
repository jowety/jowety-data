package com.jowety.data.query.jpa;

import java.util.Collection;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Exp.ExpType;
import com.jowety.data.query.Filter;

public class InBuilder  extends PredicateBuilderBase {

	public InBuilder(boolean negative) {
		super(negative);
	}

	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {

		Expression left = buildExpression(cb, from, filter.getLeftSide(), pathMap);

		if(filter.getRightSide().getType()== ExpType.LITERAL &&
				!Collection.class.isAssignableFrom(filter.getRightSide().getValue().getClass()))
			throw new RuntimeException(
					"Literal filter values for IN MatchMode must be of type java.util.Collection");

		Predicate in = null;
		if(filter.getRightSide().getType()== ExpType.LITERAL)
			in = left.in((Collection)filter.getRightSide().getValue());
		else {
			Expression right = buildExpression(cb, from, filter.getRightSide(), pathMap);
			in = left.in(right);
		}
		Predicate out = isNegative(filter.isNegative())?
				cb.not(in):
					in;
				return out;
	}

}

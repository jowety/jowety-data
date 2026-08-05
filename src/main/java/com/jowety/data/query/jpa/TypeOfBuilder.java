package com.jowety.data.query.jpa;

import java.util.Map;

import com.jowety.data.query.Filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TypeOfBuilder  extends PredicateBuilderBase {

	public TypeOfBuilder(boolean negative) {
		super(negative);
	}

	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {
		if(filter.getRightSide().getValue() instanceof Class == false) {
			throw new RuntimeException("Type filter must have a java Class object as the right-side literal");
		}		
		Expression right = buildExpression(cb, from, filter.getRightSide(), pathMap);

		Predicate out = isNegative(filter.isNegative())?
				cb.notEqual(from.type(), right):
					cb.equal(from.type(), right);
				return out;
	}

}

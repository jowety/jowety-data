package com.jowety.data.query.jpa;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Filter;

public class NullBuilder extends PredicateBuilderBase {

	public NullBuilder(boolean negative) {
		super(negative);
	}

	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {

		Expression path = buildExpression(cb, from, filter.getLeftSide(), pathMap);
		Predicate out = isNegative(filter.isNegative())?
				cb.isNotNull(path):
					cb.isNull(path);
				return out;
	}



}

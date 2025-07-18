package com.jowety.data.query.jpa;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Exp;

public abstract class PredicateBuilderBase implements PredicateBuilder{

	private boolean negative = false;

	public PredicateBuilderBase(boolean negative) {
		super();
		this.negative = negative;
	}

	public boolean isNegative(boolean filterVal) {
		//XOR of intial negativity flag state and the filter negativity flag
		//returns true if both different, but false if both are the same
		//thus, true if either flag is set, but filterVal is dynamic and will REVERSE the direction of the negative field
		return negative ^ filterVal;
	}

	public Expression<?> buildExpression(CriteriaBuilder cb, Root root, Exp exp, Map<String, Path> pathMap) {
		return JPAUtil.buildExpression(cb, root, exp, pathMap);
	}
}

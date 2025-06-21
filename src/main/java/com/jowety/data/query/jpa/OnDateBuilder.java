package com.jowety.data.query.jpa;

import java.util.Date;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Exp.ExpType;
import com.jowety.util.DateUtil;
import com.jowety.data.query.Filter;

public class OnDateBuilder  extends PredicateBuilderBase {


	public OnDateBuilder(boolean negative) {
		super(negative);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {

		if(filter.getRightSide().getType()== ExpType.LITERAL &&
				!Date.class.isAssignableFrom(filter.getRightSide().getValue().getClass()))
			throw new RuntimeException(
					"Filter values for ON_DATE MatchMode must be of type java.lang.Date");

		Expression path = buildExpression(cb, from, filter.getLeftSide(), pathMap);
		Date ondate = (Date)filter.getRightSide().getValue();
		Date start = DateUtil.getTopOfDay(ondate);
		Date end = DateUtil.getTopOfNextDay(ondate);


		Predicate out = isNegative(filter.isNegative())?

				//NOT ON DATE (< start, >= end)
				cb.and(cb.lessThan(path, start), cb.greaterThanOrEqualTo(path, end)):

					//ON DATE ( >= start, < end)
					cb.and(cb.greaterThanOrEqualTo(path, start),
							cb.lessThan(path, end));


				return out;
	}


}

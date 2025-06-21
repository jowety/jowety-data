package com.jowety.data.query.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Filter;
import com.jowety.data.query.Filter.MatchMode;

public class MasterPredicateBuilder implements PredicateGroupBuilder, PredicateBuilder{

	private Map<MatchMode, PredicateBuilder> builders = new HashMap<Filter.MatchMode, PredicateBuilder>();
	private Map<MatchMode, PredicateGroupBuilder> groupBuilders = new HashMap<Filter.MatchMode, PredicateGroupBuilder>();

	public MasterPredicateBuilder() {
		builders.put(MatchMode.NULL, new NullBuilder(false));
		builders.put(MatchMode.NOT_NULL, new NullBuilder(true));

		builders.put(MatchMode.EQUALS, new EqualsBuilder(false));
		builders.put(MatchMode.NOT_EQUAL, new EqualsBuilder(true));

		builders.put(MatchMode.GREATER_THAN, new GreaterThanBuilder(false));
		builders.put(MatchMode.GREATER_THAN_OR_EQUAL, new GreaterThanOrEqualBuilder(false));
		builders.put(MatchMode.LESS_THAN, new LessThanBuilder(false));
		builders.put(MatchMode.LESS_THAN_OR_EQUAL, new LessThanOrEqualBuilder(false));

		builders.put(MatchMode.IN, new InBuilder(false));
		builders.put(MatchMode.NOT_IN, new InBuilder(true));

		builders.put(MatchMode.LIKE, new LikeBuilder(false));
		builders.put(MatchMode.LIKE_CI, new LikeBuilderCI(false));
		builders.put(MatchMode.NOT_LIKE, new LikeBuilder(true));
		builders.put(MatchMode.NOT_LIKE_CI, new LikeBuilderCI(true));
		builders.put(MatchMode.STARTSWITH, new StartsWithBuilder(false));
		builders.put(MatchMode.STARTSWITH_CI, new StartsWithBuilderCI(false));
		builders.put(MatchMode.ENDSWITH, new EndsWithBuilder(false));
		builders.put(MatchMode.ENDSWITH_CI, new EndsWithBuilderCI(false));
		builders.put(MatchMode.CONTAINS, new ContainsBuilder(false));
		builders.put(MatchMode.DOESNT_CONTAIN, new ContainsBuilder(true));
		builders.put(MatchMode.CONTAINS_CI, new ContainsBuilderCI(false));
		builders.put(MatchMode.DOESNT_CONTAIN_CI, new ContainsBuilderCI(true));
		builders.put(MatchMode.ON_DATE, new OnDateBuilder(false));

		groupBuilders.put(MatchMode.AND, new AndBuilder(this));
		groupBuilders.put(MatchMode.OR, new OrBuilder(this));
	}


	/**
	 * Builds the entire WHERE clause for a criteria query. Uses a conjunction (AND) for the collection of filters.
	 */
	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Collection<Filter<X>> filters, Map<String, Path> pathMap) {
		PredicateGroupBuilder andBuilder = groupBuilders.get(MatchMode.AND);
		return andBuilder.build(cb, from, filters, pathMap);
	}
	/**
	 *  Gets the appropriate group builder (AND/OR) from the map delegates to it.
	 */
	public <X> Predicate buildGroup(CriteriaBuilder cb, Root<X> from, Collection<Filter<X>> filters, MatchMode mode, Map<String, Path> pathMap) {
		PredicateGroupBuilder builder = groupBuilders.get(mode);
		return builder.build(cb, from, filters, pathMap);
	}

	/**
	 *  Gets the appropriate builder from the map delegates to it.
	 */
	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Filter<X> filter, Map<String, Path> pathMap) {
		PredicateBuilder builder = builders.get(filter.getMatchMode());
		return builder.build(cb, from, filter, pathMap);
	}



}

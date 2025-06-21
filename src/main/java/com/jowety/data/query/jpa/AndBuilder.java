package com.jowety.data.query.jpa;

import java.util.Collection;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Filter;

/**
 * @author Jon.Tyree
 *
 * The AndBuilder is a group builder. It groups Predicates into AND clauses.
 * All the sub Predicate generation is run through the master builder which delegates to a builder
 * implementation based on the MatchMode.
 *
 */
public class AndBuilder implements PredicateGroupBuilder {

	private MasterPredicateBuilder master;

	public AndBuilder(MasterPredicateBuilder master) {
		super();
		this.master = master;
	}

	/**
	 * Creates the AND grouping of Predicates
	 */
	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Collection<Filter<X>> filters, Map<String, Path> pathMap) {

		Predicate where = cb.conjunction();

		if(filters != null && filters.size() > 0) {
			if(!Filter.class.isAssignableFrom(filters.iterator().next().getClass()))
				throw new RuntimeException(
						"Collection values for AND MatchMode must be of type FilterBy");

			for(Filter<X> filter: filters)
				if(filter.getMatchMode().isGouping()) {
					if(!Collection.class.isAssignableFrom(filter.getRightSide().getValue().getClass()))
						throw new RuntimeException(
								"Filter values for grouping Filters (AND/OR) must be of type java.util.Collection");

					Collection<Filter<X>> subfilters = (Collection<Filter<X>>)filter.getRightSide().getValue();
					where = cb.and(where, master.buildGroup(cb, from, subfilters, filter.getMatchMode(), pathMap));
				} else
					where = cb.and(where, master.build(cb, from, filter, pathMap));

		}
		return where;
	}


}

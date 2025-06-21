package com.jowety.data.query.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Filter;

/**
 * @author Jon.Tyree
 *
 * The OrBuilder is a group builder. It groups Predicates into OR clauses.
 * All the sub Predicate generation is run through the master builder which delegates to a builder
 * implementation based on the MatchMode.
 *
 */
public class OrBuilder implements PredicateGroupBuilder {

	private MasterPredicateBuilder master;

	public OrBuilder(MasterPredicateBuilder master) {
		super();
		this.master = master;
	}

	/**
	 * Creates the OR grouping of Predicates
	 */
	@Override
	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Collection<Filter<X>> filters, Map<String, Path> pathMap) {

		Predicate where = cb.disjunction();

		if(filters != null && filters.size() > 0) {
			if(!Filter.class.isAssignableFrom(filters.iterator().next().getClass()))
				throw new RuntimeException(
						"Collection values for OR MatchMode must be of type FilterBy");

			List<Predicate> criteria = new ArrayList<Predicate>();

			for(Filter<X> filter: filters)
				if(filter.getMatchMode().isGouping()) {
					if(!Collection.class.isAssignableFrom(filter.getRightSide().getValue().getClass()))
						throw new RuntimeException(
								"Filter values for grouping Filters (AND/OR) must be of type java.util.Collection");

					Collection<Filter<X>> subfilters = (Collection<Filter<X>>)filter.getRightSide().getValue();
					Predicate p = master.buildGroup(cb, from, subfilters, filter.getMatchMode(), pathMap);
					criteria.add(p);
				}
				else {
					Predicate p = master.build(cb, from, filter, pathMap);
					criteria.add(p);
				}
			//doing cb.or to each Predicate in the loop did not work properly, even though it should,
			//but batching them up into an array works fine.
			where = cb.or(criteria.toArray(new Predicate[0]));

		}
		return where;
	}


}

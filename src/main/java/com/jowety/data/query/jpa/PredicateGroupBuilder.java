package com.jowety.data.query.jpa;

import java.util.Collection;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.jowety.data.query.Filter;

public interface PredicateGroupBuilder {

	public <X> Predicate build(CriteriaBuilder cb, Root<X> from, Collection<Filter<X>> filters, Map<String, Path> pathMap);
}

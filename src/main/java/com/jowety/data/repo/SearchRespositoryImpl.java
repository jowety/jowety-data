package com.jowety.data.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.jowety.data.query.Search;
import com.jowety.data.query.jpa.JPAUtil;
import com.jowety.data.query.jpa.MasterPredicateBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SearchRespositoryImpl<T, ID> 
extends SimpleJpaRepository<T, ID> 
implements SearchRepository<T, ID> {

	private final EntityManager em;
	protected final Class<T> entityType;
	private MasterPredicateBuilder whereBuilder = new MasterPredicateBuilder();

	SearchRespositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {

	    super(entityInformation, entityManager);
		this.em = entityManager;
		entityType = entityInformation.getJavaType();
	}

	/**
	 * @param search
	 * @return Typed List
	 */
	@Override
	public List<T> search(Search<T> s) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityType);
		Root<T> from = cq.from(entityType);
		cq.select(from);
		// distinct
		cq = cq.distinct(s.getDistinct());

		Map<String, Path> pathMap = new HashMap<>();
		// build the where clause
		if (s.getFilters() != null) {
			Predicate where = whereBuilder.build(cb, from, s.getFilters(), pathMap);
			cq.where(where);
		}

		// orderings
		if (s.getOrders() != null) {
			List<Order> orderings = JPAUtil.createOrderings(cb, from, s.getOrders(), pathMap);
			cq.orderBy(orderings);
		}

		TypedQuery<T> q = em.createQuery(cq);

		// hints
		if (s.getHints() != null)
			JPAUtil.setHints(q, from, s.getHints());

		// first and max results
		if (s.getFirstResult() != null)
			q.setFirstResult(s.getFirstResult());
		if (s.getMaxResults() != null)
			q.setMaxResults(s.getMaxResults());

		List<T> results = q.getResultList();

//		if (s.getEagerFetchProps() != null)
//			callGetters(results, s.getEagerFetchProps());

		return results;
	}
}

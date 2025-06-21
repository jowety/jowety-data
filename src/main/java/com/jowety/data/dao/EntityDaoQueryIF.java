package com.jowety.data.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface EntityDaoQueryIF<T> {

	public List<T> findAll();

	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @return
	 */
	public T findById(Serializable id) ;
	public T findById(Serializable id, Collection<String> eagerFetchProps);
	public T findById(Serializable id, String... eagerFetchProps);

	/**
	 * @param JPQL A JPQL query string
	 * @return The Entity type of the DAO
	 */
	public T findOne(String JPQL);

	public List<T> jpqlTypedQuery(String JPQL);
	public List<T> jpqlTypedQueryPaginated(String JPQL, int first, int max);
	public List<T> jpqlTypedQuery(String JPQL, Object...params);
	public T jpqlTypedSingleResult(String JPQL, Object...params);
	public List<T> jpqlTypedQueryPaginated(String JPQL, int first, int max, Object...params);

	/**
	 * Refresh the state of the instance from the database,
	 * overwriting changes made to the entity, if any.
	 * @param entity
	 */
	public void refresh(T entity) ;
}

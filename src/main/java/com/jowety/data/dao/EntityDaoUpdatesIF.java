package com.jowety.data.dao;

import java.io.Serializable;
import java.util.Collection;

import jakarta.persistence.LockModeType;

public interface EntityDaoUpdatesIF<T> {

	//Transactional Methods

	/**
	 * Persists the object
	 * @param t The Entity to persist
	 * @return
	 */
	public T save(T t);

	/**
	 * Merges a detached persistent object back into
	 * the persistance context, causing any changes to be saved.
	 * Non-detached objects are automatically updated when modified.
	 * @param t
	 * @return
	 */
	public T update(T t) ;

	/**
	 * Removes the object from the persistance context
	 * @param t The Entity to remove
	 * @return
	 */
	public boolean delete(T t);
	/**
	 * Removes the object from the persistance context
	 * @param id The id of the Entity to remove
	 * @return
	 */
	public boolean delete(Serializable id);


	/**
	 * Refresh the state of the instance from the database,
	 * overwriting changes made to the entity, if any,
	 * and lock it with respect to given lock mode type.
	 * @param entity
	 * @param lockMode
	 */
	public void refresh(T entity, LockModeType lockMode);

	public void lock(T entity, LockModeType lockMode);

	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @param lockMode
	 * @return
	 */
	public T findById(Serializable id, LockModeType lockMode);
	public T findById(Serializable id, LockModeType lockMode, Collection<String> eagerFetchProps);
	public T findById(Serializable id, LockModeType lockMode, String... eagerFetchProps);


	/**
	 * Flushes all pending changes to the database.
	 */
	public void flush();
}

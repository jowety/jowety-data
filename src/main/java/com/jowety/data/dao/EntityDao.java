package com.jowety.data.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jowety.data.query.Hint;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import ognl.Ognl;
import ognl.OgnlException;

public class EntityDao<T>  implements EntityDaoIF<T>, SqlDaoIF{

	protected final Class<T> daoType;

	/** The em. */
	@PersistenceContext
	protected EntityManager em;

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public EntityManager getEm() {
		return em;
	}

	@SuppressWarnings("unchecked")
	public EntityDao() {
		daoType = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}
	public EntityDao(Class<T> daoType) {
		this.daoType = daoType;
	}

	@Override
	public Class<T> getDaoType() {
		return daoType;
	}

	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @param lockMode
	 * @return
	 */
	@Override
	public T findById(Serializable id, LockModeType lockMode) {
		EntityManager em = getEm();
		T t = em.find(daoType, id, lockMode);
		return t;
	}
	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @param lockMode
	 * @param eagerFetchProps
	 * @return
	 */
	@Override
	public T findById(Serializable id, LockModeType lockMode, Collection<String> eagerFetchProps) {
		EntityManager em = getEm();
		T t = em.find(daoType, id, lockMode);
		callGetters(t, eagerFetchProps);
		return t;
	}

	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @param lockMode
	 * @param eagerFetchProps
	 * @return
	 */
	@Override
	public T findById(Serializable id, LockModeType lockMode, String... eagerFetchProps) {
		return findById(id, lockMode, Arrays.asList(eagerFetchProps));
	}

	//******** CORE CRUD METHODS ************************************

	/**
	 * Persists the object
	 * @param t The Entity to persist
	 * @return
	 */
	@Override
	public T save(T t) {
		return saveInternal(t);
	}

	private T saveInternal(T t) {
		EntityManager em = getEm();
		if(em.contains(t))
			t = em.merge(t);
		else
			em.persist(t);
		return t;
	}

	/**
	 * Merges a detached persistent object back into
	 * the persistance context, causing any changes to be saved.
	 * Non-detached objects are automatically updated when modified.
	 * @param t The Entity
	 * @return The Entity
	 */
	@Override
	public T update(T t) {
		return updateInternal(t);
	}

	private T updateInternal(T t) {
		EntityManager em = getEm();
		if(t != null) t = em.merge(t);
		return t;
	}


	/**
	 * Removes the object from the persistance context
	 * @param id The id of the Entity to remove
	 * @return
	 */
	@Override
	public boolean delete(Serializable id) {
		EntityManager em = getEm();
		if(id == null) return false;
		T x = this.findById(id);
		if(x != null) {
			em.remove(x);
			return !em.contains(x);
		}
		else return false;
	}


	/**
	 * Removes the object from the persistance context
	 * @param t The Entity to remove
	 * @return
	 */
	@Override
	public boolean delete(T t) {
		EntityManager em = getEm();
		if(t == null) return false;
		T x = em.merge(t);
		em.remove(x);
		return !em.contains(t);
	}


	@Override
	public void lock(T entity, LockModeType lockMode) {
		EntityManager em = getEm();
		em.lock(entity, lockMode);
	}

	/**
	 * Refresh the state of the instance from the database,
	 * overwriting changes made to the entity, if any,
	 * and lock it with respect to given lock mode type.
	 * @param entity
	 * @param lockMode
	 */
	@Override
	public void refresh(T entity, LockModeType lockMode) {
		EntityManager em = getEm();
		em.refresh(entity, lockMode);
	}

	/**
	 * @param JPQL A JPQL query string
	 * @return A single entity
	 */
	@Override
	public T findOne(String JPQL) {
		try {
			TypedQuery<T> tq =getEm().createQuery(JPQL, daoType);
			return tq.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * @param JPQL query sting
	 * @return Typed List
	 */
	@Override
	public List<T> jpqlTypedQuery(String JPQL){
		TypedQuery<T> tq =getEm().createQuery(JPQL, daoType);
		return tq.getResultList();
	}

	/**
	 * @param JPQL query string
	 * @param first Rownum to start
	 * @param max Max results returned
	 * @return Typed List
	 */
	@Override
	public List<T> jpqlTypedQueryPaginated(String JPQL, int first, int max){
		TypedQuery<T> tq =getEm().createQuery(JPQL, daoType);
		tq.setFirstResult(first);
		tq.setMaxResults(max);
		return tq.getResultList();
	}

	/**
	 * @param JPQL query stsring
	 * @param params varargs params for the query IN ORDER
	 * @return typed list
	 */
	@Override
	public List<T> jpqlTypedQuery(String JPQL, Object...params){
		TypedQuery<T> tq =getEm().createQuery(JPQL, daoType);
		for(int i = 1; i <= params.length; i++) {
			Object o = params[i - 1];
			if(o instanceof Date)
				tq.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
			else if(o instanceof Calendar)
				tq.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
			else tq.setParameter(i, o);
		}
		return tq.getResultList();
	}

	/**
	 * @param JPQL query stsring
	 * @param params varargs params for the query IN ORDER
	 * @return typed list
	 */
	@Override
	public T jpqlTypedSingleResult(String JPQL, Object...params){
		try {
			TypedQuery<T> tq =getEm().createQuery(JPQL, daoType);
			for(int i = 1; i <= params.length; i++) {
				Object o = params[i - 1];
				if(o instanceof Date)
					tq.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
				else if(o instanceof Calendar)
					tq.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
				else tq.setParameter(i, o);
			}
			return tq.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * @param JPQL query string
	 * @param first Rownum to start
	 * @param max Max results returned
	 * @param params varargs params for the query IN ORDER
	 * @return
	 */
	@Override
	public List<T> jpqlTypedQueryPaginated(String JPQL, int first, int max, Object...params){
		TypedQuery<T> tq =getEm().createQuery(JPQL, daoType);
		for(int i = 1; i <= params.length; i++) {
			Object o = params[i - 1];
			if(o instanceof Date)
				tq.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
			else if(o instanceof Calendar)
				tq.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
			else tq.setParameter(i, o);
		}
		tq.setFirstResult(first);
		tq.setMaxResults(max);
		return tq.getResultList();
	}

	/**
	 * Refresh the state of the instance from the database,
	 * overwriting changes made to the entity, if any.
	 * @param entity
	 */
	@Override
	public void refresh(T entity) {
		getEm().refresh(entity);
	}

	/**
	 * @return All the entities of this type
	 */
	@Override
	public List<T> findAll(){
		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(daoType);
		Root<T> from = cq.from(daoType);
		cq.select(from);
		TypedQuery<T> q = em.createQuery(cq);
		List<T> result = q.getResultList();
		return result;
	}

	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @return
	 */
	@Override
	public T findById(Serializable id) {
		EntityManager em = getEm();
		T t = em.find(daoType, id);
		if(t != null && em.contains(t))
			em.refresh(t);
		return t;
	}

	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @param eagerFetchProps
	 * @return
	 */
	@Override
	public T findById(Serializable id, Collection<String> eagerFetchProps) {
		EntityManager em = getEm();
		T t = em.find(daoType, id);
		if(t != null && em.contains(t)) {
			em.refresh(t);
			callGetters(t, eagerFetchProps);
		}
		return t;
	}

	/**
	 * Generic find-by-id method for any type of primary key
	 * @param id
	 * @param eagerFetchProps
	 * @return
	 */
	@Override
	public T findById(Serializable id, String... eagerFetchProps) {
		return findById(id, Arrays.asList(eagerFetchProps));
	}


	@Override
	public void callGetters(Collection<?> entities, Collection<String> properties) {
		if(entities != null)
			for(Object entity: entities)
				callGetters(entity, properties);
	}

	@Override
	public void callGetters(Object entity, Collection<String> properties) {
		if(properties != null)
			for(String s: properties)
				try {
					Object o = Ognl.getValue(s, entity);
					if(o != null)
						if(Collection.class.isAssignableFrom(o.getClass())) {
							Collection c = (Collection)o;
							c.iterator();
						}
						else if(Map.class.isAssignableFrom(o.getClass())) {
							Map c = (Map)o;
							c.values().iterator();
						}
				} catch (OgnlException e) {
					throw new DaoException("Invalid property name: " + s + " for entity: " + entity.toString(), e);
				}
	}

	@Override
	public void flush() {
		getEm().flush();
	}


	/* Override this in concrete DAO class to set class level hints
	 */
	@Override
	public List<Hint> getClassHints(){
		return null;
	}

	@Override
	public <R> List<R> sqlTypedQuery(String SQL, Class<R> t, Object... params) {
		Query q = em.createNativeQuery(SQL, t);
		for(int i = 1; i <= params.length; i++) {
			Object o = params[i - 1];
			if(o instanceof Date)
				q.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
			else if(o instanceof Calendar)
				q.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
			else q.setParameter(i, o);
		}
		return q.getResultList();
	}

	@Override
	public long getNextSeqValue(String sequence) {
		BigDecimal val = (BigDecimal)sqlUntypedSingleResult("select " + sequence + ".nextval from DUAL");
		return val.longValue();
	}

	/* (non-Javadoc)
	 * @see mil.usmc.tso.soda.dao.GenericDao#jpqlUntypedSingleResult(java.lang.String)
	 */
	@Override
	public Object sqlUntypedSingleResult(String SQL, Object...params) {
		try {
			Query q = em.createNativeQuery(SQL);
			for(int i = 1; i <= params.length; i++) {
				Object o = params[i - 1];
				if(o instanceof Date)
					q.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
				else if(o instanceof Calendar)
					q.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
				else q.setParameter(i, o);
			}
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public <R> R sqlTypedSingleResult(String SQL, Class<R> r, Object...params) {
		try {
			Query q = em.createNativeQuery(SQL, r);
			for(int i = 1; i <= params.length; i++) {
				Object o = params[i - 1];
				if(o instanceof Date)
					q.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
				else if(o instanceof Calendar)
					q.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
				else q.setParameter(i, o);
			}
			return (R)q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see mil.usmc.tso.soda.dao.GenericDao#jpqlUnTypedQuery(java.lang.String, java.lang.Object)
	 */
	@Override
	public List<?> sqlUntypedQuery(String SQL, Object...params){
		Query q = em.createNativeQuery(SQL);
		for(int i = 1; i <= params.length; i++) {
			Object o = params[i - 1];
			if(o instanceof Date)
				q.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
			else if(o instanceof Calendar)
				q.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
			else q.setParameter(i, o);
		}
		return q.getResultList();
	}

	@Override
	public int sqlUpdate(String SQL, Object...params) {
		EntityManager em = getEm();
		Query q = em.createNativeQuery(SQL);
		for(int i = 1; i <= params.length; i++) {
			Object o = params[i - 1];
			if(o instanceof Date)
				q.setParameter(i, (Date)o, TemporalType.TIMESTAMP);
			else if(o instanceof Calendar)
				q.setParameter(i, (Calendar)o, TemporalType.TIMESTAMP);
			else q.setParameter(i, o);
		}
		return q.executeUpdate();
	}
}

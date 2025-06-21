package com.jowety.data.dao;

import java.util.Collection;
import java.util.List;

import com.jowety.data.query.Hint;

import jakarta.persistence.EntityManager;

public interface EntityDaoIF<T> extends EntityDaoQueryIF<T>, EntityDaoUpdatesIF<T>{

	public void setEntityManager(EntityManager em);
	public EntityManager getEm();

	public Class<T> getDaoType();

	public void callGetters(Collection<?> entities, Collection<String> properties);

	public void callGetters(Object entity, Collection<String> properties);

	public List<Hint> getClassHints();
}

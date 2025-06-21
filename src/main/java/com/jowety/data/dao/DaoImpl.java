/**
 *
 */
package com.jowety.data.dao;

/**
 * @author Jon.Tyree
 *
 * Base DAO implementation class that can be used with any JPA persistent entity.
 */
public class DaoImpl<T> extends SearchDao<T> implements DaoIF<T>{

	public DaoImpl() {
		super();
	}

	public DaoImpl(Class<T> daoType) {
		super(daoType);
	}



}

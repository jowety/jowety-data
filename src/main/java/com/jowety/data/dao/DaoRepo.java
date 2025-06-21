package com.jowety.data.dao;


public interface DaoRepo {

	public <T> DaoIF<T> getDao(Class<T> clash);
}

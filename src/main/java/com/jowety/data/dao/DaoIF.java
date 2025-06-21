/**
 *
 */
package com.jowety.data.dao;

import com.jowety.data.query.SearchDaoIF;

/**
 * DAO interface geared toward JPA/ORM persistence.
 *
 *
 * @author Jon.Tyree
 *
 */
public interface DaoIF<T> extends SqlDaoIF, SearchDaoIF<T>{

}

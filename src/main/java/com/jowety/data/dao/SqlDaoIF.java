package com.jowety.data.dao;

import java.util.List;

public interface SqlDaoIF {

	/**
	 * @param SQL The SQL String
	 * @param r The returned Entity type (only works for Entities, not column types)
	 * @param params Optional parameters (use ?1, ?2, etc in the SQL)
	 * @return Single result of type R
	 */
	public <R> R sqlTypedSingleResult(String SQL, Class<R> r, Object...params);

	/**
	 * @param SQL The SQL String
	 * @param params Optional parameters (use ?1, ?2, etc in the SQL)
	 * @return Untyped single result
	 */
	public Object sqlUntypedSingleResult(String SQL, Object...params);

	/**
	 * @param SQL The SQL String
	 * @param r The returned Entity type (only works for Entities, not column types)
	 * @param params Optional parameters (use ?1, ?2, etc in the SQL)
	 * @return List of type R
	 */
	public <R> List<R> sqlTypedQuery(String sql, Class<R> r, Object... params);

	/**
	 * @param SQL The SQL String
	 * @param r The returned Entity type (only works for Entities, not column types)
	 * @param params Optional parameters (use ?1, ?2, etc in the SQL)
	 * @return List of unknown type
	 */
	public List<?> sqlUntypedQuery(String JPQL, Object... params);

	public long getNextSeqValue(String sequence);

	public int sqlUpdate(String SQL, Object...params);
}

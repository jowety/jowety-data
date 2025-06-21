/**
 *
 */
package com.jowety.data.query;

import java.io.Serializable;

/**
 * @author Jon.Tyree
 *
 */
public class OrderBy<T> implements Serializable{

	private static final long serialVersionUID = 5154783024912486179L;

	private Exp expression;
	private boolean ascending = true;
	private boolean outerjoin = false;

	public OrderBy() {
	}

	/**
	 * @param property The object property name to sort on
	 * @param ascending The sort direction. Set false for DESC
	 * @param outerjoin True will set a LEFT OUTER JOIN for the FIRST node in the property path. Only applies to nested properties!
	 */
	public OrderBy(Exp expression, boolean ascending, boolean outerjoin) {
		this.expression = expression;
		this.ascending = ascending;
		this.outerjoin = outerjoin;
	}
	/**
	 * @param property The object property name to sort on
	 * @param ascending The sort direction. Set false for DESC
	 * @param outerjoin True will set a LEFT OUTER JOIN for the FIRST node in the property path. Only applies to nested properties!
	 */
	public OrderBy(String property, boolean ascending, boolean outerjoin) {
		this.expression = Exp.path(property);
		this.ascending = ascending;
		this.outerjoin = outerjoin;
	}

	/**
	 * @param property The object property name to sort on
	 * @param ascending The sort direction. Set false for DESC
	 */
	public OrderBy(Exp expression, boolean ascending) {
		super();
		this.expression = expression;
		this.ascending = ascending;
	}

	/**
	 * @param property The object property name to sort on
	 * @param ascending The sort direction. Set false for DESC
	 */
	public OrderBy(String property, boolean ascending) {
		super();
		this.expression = Exp.path(property);
		this.ascending = ascending;
	}
	/**
	 * Sort direction is ASC for this constructor
	 * @param property The object property name to sort on
	 */
	public OrderBy(Exp expression) {
		super();
		this.expression = expression;
		this.ascending = true;
	}
	/**
	 * Sort direction is ASC for this constructor
	 * @param property The object property name to sort on
	 */
	public OrderBy(String property) {
		super();
		this.expression = Exp.path(property);
		this.ascending = true;
	}
	public Exp getExpression() {
		return expression;
	}

	public void setExpression(Exp expression) {
		this.expression = expression;
	}

	/**
	 * @return the ascending
	 */
	public boolean isAscending() {
		return ascending;
	}
	/**
	 * @param ascending
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isOuterjoin() {
		return outerjoin;
	}

	public void setOuterjoin(boolean outerjoin) {
		this.outerjoin = outerjoin;
	}



}

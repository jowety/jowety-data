package com.jowety.data.query;

import java.io.Serializable;

public class Hint  implements Serializable{

	private static final long serialVersionUID = 3512993167688805352L;

	private String name;
	private Object value;
	private boolean prependRootAlias = false;

	public Hint() {
	}
	public Hint(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	public Hint(String name, Object value, boolean prependRootAlias) {
		this(name, value);
		this.prependRootAlias = prependRootAlias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean getPrependRootAlias() {
		return prependRootAlias;
	}

	/**
	 * This will prepend the entity alias in the query
	 * plus a dot to the hint value. Set true for hints on entity properties,
	 *  
	 * @param prependRootAlias
	 */
	public void setPrependRootAlias(boolean prependRootAlias) {
		this.prependRootAlias = prependRootAlias;
	}



}

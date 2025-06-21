package com.jowety.data.query;

import java.io.Serializable;

/**
 * @author Jon.Tyree
 * Ultimately gets translated into a jakarta.persistence.criteria.Expression
 *
 */
public class Exp implements Serializable{

	private static final long serialVersionUID = 1L;

	public Exp() {
	}
	
	private ExpType type;
	//Value = one of function, path, or literal
	private Function function;
	private String path;
	private Object literal;
	
	public static enum ExpType{
		FUNCTION,
		PATH,
		LITERAL
	}
	
	public static Exp function(Function func) {
		Exp out = new Exp();
		out.type = ExpType.FUNCTION;
		out.function = func;
		return out;
	}
	public static Exp path(String path) {
		Exp out = new Exp();
		out.type = ExpType.PATH;
		out.path = path;
		return out;		
	}
	public static Exp literal(Object value) {
		Exp out = new Exp();
		out.type = ExpType.LITERAL;
		out.literal = value;
		return out;		
	}
	/**
	 * @return the type
	 */
	public ExpType getType() {
		return type;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		switch (type) {
		case FUNCTION: {
			return function;
		}
		case PATH: {
			return path;
		}
		case LITERAL: {
			return literal;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ExpType type) {
		this.type = type;
	}
//	/**
//	 * @param value the value to set
//	 */
//	public void setValue(Object value) {
//		this.value = value;
//	}
	public Function getFunction() {
		return function;
	}
	public void setFunction(Function function) {
		this.function = function;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Object getLiteral() {
		return literal;
	}
	public void setLiteral(Object literal) {
		this.literal = literal;
	}
	

}

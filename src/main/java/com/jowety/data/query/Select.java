package com.jowety.data.query;

import java.io.Serializable;

/**
 * @author Jon.Tyree
 *
 */
public class Select  implements Serializable{

	public static enum Aggregate{
		MIN,
		MAX,
		COUNT,
		AVG,
		SUM
	}

	private Exp expression;
	private Aggregate aggregate;
	private String display;

	public Select() {
	}

	public Select(Function func) {
		this.expression = Exp.function(func);
	}
	/**
	 * If you just want a path selection (property/field), use this constructor.
	 * @param path
	 */
	public Select(String path) {
		this.expression = Exp.path(path);
	}	
	public Select(String path, String display) {
		this.expression = Exp.path(path);
		this.display = display;
	}
	public Select(Exp expression) {
		this.expression = expression;
	}
	public Select(Exp expression, String display) {
		this.expression = expression;
		this.display = display;
	}
	public Select(Aggregate agg, String path) {
		this.expression = Exp.path(path);
		this.aggregate = agg;
	}
	public Select(Aggregate agg, String path, String display) {
		this.expression = Exp.path(path);
		this.aggregate = agg;
		this.display = display;
	}

	public static Select path(String path) {
		return new Select(path);
	}
	public static Select path(String path, String display) {
		return new Select(path, display);
	}
	public static Select count(String path) {
		return new Select(Aggregate.COUNT, path);
	}
	public static Select count(String path, String display) {
		return new Select(Aggregate.COUNT, path, display);
	}
	public static Select min(String path) {
		return new Select(Aggregate.MIN, path);
	}
	public static Select min(String path, String display) {
		return new Select(Aggregate.MIN, path, display);
	}
	public static Select max(String path) {
		return new Select(Aggregate.MAX, path);
	}
	public static Select max(String path, String display) {
		return new Select(Aggregate.MAX, path, display);
	}
	public static Select avg(String path) {
		return new Select(Aggregate.AVG, path);
	}
	public static Select avg(String path, String display) {
		return new Select(Aggregate.AVG, path, display);
	}
	public static Select sum(String path) {
		return new Select(Aggregate.SUM, path);
	}
	public static Select sum(String path, String display) {
		return new Select(Aggregate.SUM, path, display);
	}
	public static Select function(Function f) {
		return new Select(Exp.function(f));
	}
	public static Select function(Function f, String display) {
		return new Select(Exp.function(f), display);
	}

	/**
	 * @return the expression
	 */
	public Exp getExpression() {
		return expression;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(Exp expression) {
		this.expression = expression;
	}

	public Aggregate getAggregate() {
		return aggregate;
	}

	public void setAggregate(Aggregate aggregate) {
		this.aggregate = aggregate;
	}

	public String getDisplay() {
		if(display != null) return display;
		else return expression.getValue().toString();
	}

	public void setDisplay(String display) {
		this.display = display;
	}

}

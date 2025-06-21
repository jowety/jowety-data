package com.jowety.data.query;

import java.util.ArrayList;
import java.util.Collection;

public class OrGroup<T> extends Filter<T> {

	private Collection<Filter<T>> filters = new ArrayList<Filter<T>>();
	private Search<T> search;

	public OrGroup(Search<T> s) {
		super();
		this.setRightSide(Exp.literal(filters));
		this.setMatchMode(MatchMode.OR);
		this.search = s;
	}
	public OrGroup<T> addFilter(Filter<T> filter){
		if(this.filters == null)
			this.filters = new ArrayList<Filter<T>>();
		this.filters.add(filter);
		return this;
	}
	public OrGroup<T> Filter(String path, Object value, MatchMode match){
		this.addFilter(new Filter<T>(path, value, match));
		return this;
	}
	public OrGroup<T> Filter(Function func, Object value, MatchMode match){
		this.addFilter(new Filter<T>(func, value, match));
		return this;
	}
	public OrGroup<T> Filter(Exp exp, Object value, MatchMode match){
		this.addFilter(new Filter<T>(exp, value, match));
		return this;
	}
	public OrGroup<T> filterPaths(String leftPath, String rightPath, MatchMode match){
		this.addFilter(new Filter<T>(Exp.path(leftPath), Exp.path(rightPath), match));
		return this;
	}
	/**
	 * Convenience method for adding a Filter with MatchMode.EQUALS
	 * @param path the property path to filter by
	 * @param value the value to restrict the property
	 * @return
	 */
	public OrGroup<T> Filter(String path, Object value){
		this.addFilter(new Filter<T>(path, value, MatchMode.EQUALS));
		return this;
	}
	public OrGroup<T> notNull(String path){
		this.addFilter(new Filter<T>(path, null, MatchMode.NOT_NULL));
		return this;
	}
	public OrGroup<T> isNull(String path){
		this.addFilter(new Filter<T>(path, null, MatchMode.NULL));
		return this;
	}
	public OrGroup<T> Filter(Function func, Object value){
		this.addFilter(new Filter<T>(func, value, MatchMode.EQUALS));
		return this;
	}
	public OrGroup<T> Filter(Exp exp, Object value){
		this.addFilter(new Filter<T>(exp, value, MatchMode.EQUALS));
		return this;
	}
	public OrGroup<T> Filter(Collection<Filter<T>> value, MatchMode match){
		this.addFilter(new Filter<T>(value, match));
		return this;
	}

	public Search<T> endOrGroup(){
		return search;
	}
}

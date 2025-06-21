package com.jowety.data.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.jowety.data.query.Filter.MatchMode;
import com.jowety.util.DateUtil;

import jakarta.persistence.Tuple;


public class Search<T> implements Serializable{

	private static final long serialVersionUID = 1L;

	String title;
	protected List<Select> selects;
	protected Collection<Filter<T>> filters;
	protected List<OrderBy<T>> orders;
	protected List<Exp> groups;
	protected Collection<Hint> hints;
	protected Integer firstResult;
	protected Integer maxResults;
	protected boolean distinct;
	protected Collection<String> eagerFetch;

	protected SearchDaoIF<T> dao;
	
	public Search() {
	}
	public Search(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean getDistinct() {
		return distinct;
	}
	public Search<T> setDistinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}
	/**
	 * Convenience method for setting distict = true
	 * @return
	 */
	public Search<T> distinct() {
		this.distinct = true;
		return this;
	}
	public List<Select> getSelects(){
		return selects;
	}
	public void setSelects(List<Select> selects) {
		this.selects = selects;
	}
	public Collection<Filter<T>> getFilters() {
		return filters;
	}
	public Search<T> setFilters(Collection<Filter<T>> filters) {
		this.filters = filters;
		return this;
	}
	public Search<T> setFilters(Filter<T>... Filter) {
		this.filters = Arrays.asList(Filter);
		return this;
	}
	public List<OrderBy<T>> getOrders() {
		return orders;
	}
	public Search<T> setOrders(List<OrderBy<T>> orders) {
		this.orders = orders;
		return this;
	}
	public Search<T> setOrders(OrderBy<T>... orders) {
		this.orders = Arrays.asList(orders);
		return this;
	}
	public List<Exp> getGroups() {
		return groups;
	}
	public void setGroups(List<Exp> groups) {
		this.groups = groups;
	}
	public Collection<Hint> getHints() {
		return hints;
	}
	public Search<T> setHints(Collection<Hint> hints) {
		this.hints = hints;
		return this;
	}
	public Search<T> setHints(Hint... hints) {
		this.hints = Arrays.asList(hints);
		return this;
	}
	public Integer getFirstResult() {
		return firstResult;
	}
	public Search<T> setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
		return this;
	}
	/**
	 * Same as setFirstResult, but shorter
	 * @param firstResult
	 * @return
	 */
	public Search<T> first(Integer firstResult) {
		this.firstResult = firstResult;
		return this;
	}
	public Integer getMaxResults() {
		return maxResults;
	}
	public Search<T> setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	/**
	 * Same as setMaxResults, but shorter
	 * @param maxResults
	 * @return
	 */
	public Search<T> max(Integer maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	public Search<T> addFilter(Filter<T> filter){
		if(this.filters == null)
			this.filters = new ArrayList<>();
		this.filters.add(filter);
		return this;
	}
	/**
	 * Convenience method for adding a Filter
	 * @param path
	 * @param value
	 * @param match
	 * @return
	 */
	public Search<T> filter(String path, Object value, MatchMode match){
		this.addFilter(new Filter<T>(path, value, match));
		return this;
	}
	public Search<T> filter(Function func, Object value, MatchMode match){
		this.addFilter(new Filter<T>(func, value, match));
		return this;
	}
	public Search<T> filter(Exp exp, Object value, MatchMode match){
		this.addFilter(new Filter<T>(exp, value, match));
		return this;
	}
	public Search<T> filterPaths(String leftPath, String rightPath, MatchMode match){
		this.addFilter(new Filter<T>(Exp.path(leftPath), Exp.path(rightPath), match));
		return this;
	}
	/**
	 * @param property
	 * @param fromDate GREATER_THAN_OR_EQUAL match. If null, no fromDate filter added.
	 * @param toDate LESS_THAN match. If null, no toDate filter added.
	 * @return
	 */
	public Search<T> filterDateRange(String property, Date fromDate, Date toDate){
		if(fromDate != null) filter(property, DateUtil.getTopOfDay(fromDate), MatchMode.GREATER_THAN_OR_EQUAL);
		if(toDate != null) filter(property, DateUtil.getTopOfNextDay(toDate), MatchMode.LESS_THAN);
		return this;
	}
	/**
	 * Convenience method to create an OR grouping
	 * @param filters
	 * @return
	 */
	public Search<T> createOrGroup(Filter<T>... filters){
		Collection<Filter<T>> orSet = new HashSet<>();
		for(Filter<T> filter: filters)
			orSet.add(filter);
		this.addFilter(new Filter<T>("", orSet, MatchMode.OR));
		return this;
	}

	public OrGroup<T> beginOrGroup(){
		OrGroup<T> og = new OrGroup<>(this);
		this.addFilter(og);
		return og;
	}

	/**
	 * Convenience method for adding a Filter with MatchMode.EQUALS
	 * @param path the property path to filter by
	 * @param value the value to restrict the property
	 * @return
	 */
	public Search<T> filter(String path, Object value){
		this.addFilter(new Filter<T>(path, value, MatchMode.EQUALS));
		return this;
	}
	public Search<T> notNull(String path){
		this.addFilter(new Filter<T>(path, null, MatchMode.NOT_NULL));
		return this;
	}
	public Search<T> isNull(String path){
		this.addFilter(new Filter<T>(path, null, MatchMode.NULL));
		return this;
	}
	public Search<T> filter(Function func, Object value){
		this.addFilter(new Filter<T>(func, value, MatchMode.EQUALS));
		return this;
	}
	public Search<T> filter(Exp exp, Object value){
		this.addFilter(new Filter<T>(exp, value, MatchMode.EQUALS));
		return this;
	}
	public Search<T> filter(Collection<Filter<T>> value, MatchMode match){
		this.addFilter(new Filter<>(value, match));
		return this;
	}
	public Search<T> addOrderBy(OrderBy<T> orderby){
		if(this.orders == null)
			this.orders = new ArrayList<>();
		this.orders.add(orderby);
		return this;
	}
	/**
	 * Convenience method for adding an OrderBy
	 * @param orderby
	 * @return
	 */
	public Search<T> orderBy(String property, boolean ascending){
		if(this.orders == null)
			this.orders = new ArrayList<>();
		this.orders.add(new OrderBy<T>(property, ascending));
		return this;
	}
	public Search<T> orderByAsc(String property){
		return orderBy(property, true);
	}
	public Search<T> orderByDesc(String property){
		return orderBy(property, false);
	}

	public Search<T> addGroupByPath(String path){
		if(this.groups == null)
			this.groups = new ArrayList<>();
		this.groups.add(Exp.path(path));
		return this;
	}
	public Search<T> addGroupByFunction(Function func){
		if(this.groups == null)
			this.groups = new ArrayList<>();
		this.groups.add(Exp.function(func));
		return this;
	}

	public Search<T> addHint(Hint hint){
		if(this.hints == null) this.hints = new ArrayList<>();
		this.hints.add(hint);
		return this;
	}

	public Search<T> hint(String name, Object value){
		if(this.hints == null) this.hints = new ArrayList<>();
		this.hints.add(new Hint(name, value));
		return this;
	}
	/**
	 * @return the eagerFetch
	 */
	public Collection<String> getEagerFetchProps() {
		return eagerFetch;
	}
	/**
	 * @param eagerFetch
	 */
	public Search<T>  setEagerFetchProps(Collection<String> eagerFetch) {
		this.eagerFetch = eagerFetch;
		return this;
	}
	public Search<T> eagerFetch(String prop){
		if(eagerFetch == null) eagerFetch = new HashSet<>();
		eagerFetch.add(prop);
		return this;
	}

	/*
	 * 2014-07-10 JWT
	 * New convenience functionality for chaining calls.
	 * dao.newSearch() now injects the Dao into the returned Search object.
	 * Search now includes search methods which call back to the injected Dao.
	 *
	 * example - List<T> results = dao.newSearch().Filter().orderBy().search();
	 */

	public SearchDaoIF<T> getDao() {
		return dao;
	}
	public void setDao(SearchDaoIF<T> dao) {
		this.dao = dao;
	}


	public List<T> results(){
		if(dao == null)
			throw new UnsupportedOperationException("The SearchDao has not been set");
		return dao.search(this);
	}

	public Search<T> select(String... select) {
		if(selects == null)
			selects = new ArrayList<>();
		for(String s: select)
			selects.add(new Select(s));
		return this;
	}

	public Search<T> select(Select... select) {
		if(selects == null)
			selects = new ArrayList<>();
		for(Select s: select)
			selects.add(s);
		return this;
	}

	/**
	 * Performs a search using the selections that have been set on the Search
	 * @return List<Tuple>
	 */
	public List<Tuple> selectedResults(){
		if(dao == null)
			throw new UnsupportedOperationException("The SearchDao has not been set");
		return dao.search(selects, this);
	}
	
	public <X> List<X> singleColumnSearch(){
		if(dao == null)
			throw new UnsupportedOperationException("The SearchDao has not been set");
		return dao.singleColumnSearch(this);
	}

	public T findOne(){
		if(dao == null)
			throw new UnsupportedOperationException("The SearchDao has not been set");
		return dao.searchOne(this);
	}

	public <X> List<X> singleSelectSearch(Select select){
		if(dao == null)
			throw new UnsupportedOperationException("The SearchDao has not been set");
		return dao.search(select, this);
	}


	public Integer count() {
		if(dao == null)
			throw new UnsupportedOperationException("The SearchDao has not been set");
		return dao.count(this);
	}

	/**
	 * Starts a method chain which creates a Filter on this Search
	 * @return a LeftSide object
	 */
	public Filter<T>.LeftSide where(){
		Filter<T> f = new Filter<>();
		addFilter(f);
		return f.startWhere(this);
	}
	
	public Search<T> copy(){
		Search<T> copy = new Search<>();
		copy.selects = new ArrayList(selects);
		copy.filters = new ArrayList(filters);
		copy.groups = new ArrayList<>(groups);
		copy.firstResult = firstResult;
		copy.maxResults = maxResults;
		copy.distinct = distinct;
		copy.eagerFetch = new ArrayList<>(eagerFetch);
		return copy;
	}
}

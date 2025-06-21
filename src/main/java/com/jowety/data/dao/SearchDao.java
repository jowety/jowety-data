package com.jowety.data.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jowety.data.client.search.Report;
import com.jowety.data.client.search.ReportCell;
import com.jowety.data.client.search.ReportRow;
import com.jowety.data.client.search.RowHolder;
import com.jowety.data.client.search.SearchResult;
import com.jowety.data.client.search.SimpleSearch;
import com.jowety.data.query.Count;
import com.jowety.data.query.Exp;
import com.jowety.data.query.Filter;
import com.jowety.data.query.OrderBy;
import com.jowety.data.query.Search;
import com.jowety.data.query.SearchDaoIF;
import com.jowety.data.query.Select;
import com.jowety.data.query.Select.Aggregate;
import com.jowety.data.query.SimpleSearchConverter;
import com.jowety.data.query.jpa.JPAUtil;
import com.jowety.data.query.jpa.MasterPredicateBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SearchDao<T> extends EntityDao<T> implements SearchDaoIF<T> {


	public SearchDao() {
		super();
	}

	public SearchDao(Class<T> daoType) {
		super(daoType);
	}


	private MasterPredicateBuilder whereBuilder = new MasterPredicateBuilder();


	/**
	 * Method to return a single matching result entity.
	 * Catches the NoResultException and returns null
	 * @param filters Collection
	 * @return
	 */
	@Override
	public T findOne(Collection<Filter<T>> filters) {
		try {
			EntityManager em = getEm();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(daoType);
			Root<T> from = cq.from(daoType);
			cq.select(from);

			Map<String, Path> pathMap = new HashMap<>();
			//build the where clause
			if(filters != null) {
				Predicate where = whereBuilder.build(cb, from, filters, pathMap);
				cq.where(where);
			}
			TypedQuery<T> q = em.createQuery(cq);
			return q.getSingleResult();
		}
		catch (NoResultException  e) {
			return null;
		}
	}
	/**
	 * Method to return a single matching result entity.
	 * Catches the NoResultException and returns null
	 * @param filters varargs array
	 * @return a single entity
	 */
	@Override
	public T findOne(Filter<T>... filters) {
		return findOne(Arrays.asList(filters));
	}

	@Override
	public Search<T> search(){
		Search<T> out = new Search<>();
		out.setDao(this);
		return out;
	}


	@Override
	public List<Select> makeSelects(String...args){
		List<Select> selects = new ArrayList<>();
		for(String s: args)
			selects.add(new Select(s));
		return selects;
	}


	/**
	 * @param search
	 * @return Typed List
	 * SELECTS ARE IGNORED IN THIS METHOD BECAUSE IT RETURNS THE ENTITY TYPE
	 */
	@Override
	public List<T> search(Search<T> s){

		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(daoType);
		Root<T> from = cq.from(daoType);
		cq.select(from);
		//distinct
		cq = cq.distinct(s.getDistinct());

		Map<String, Path> pathMap = new HashMap<>();
		//build the where clause
		if(s.getFilters() != null) {
			Predicate where = whereBuilder.build(cb, from, s.getFilters(), pathMap);
			cq.where(where);
		}

		//orderings
		if(s.getOrders() != null) {
			List<Order> orderings = JPAUtil.createOrderings(cb, from, s.getOrders(), pathMap);
			cq.orderBy(orderings);
		}

		TypedQuery<T> q = em.createQuery(cq);

		//hints
		if(s.getHints() != null)
			JPAUtil.setHints(q, from, s.getHints());

		//first and max results
		if(s.getFirstResult() != null) q.setFirstResult(s.getFirstResult());
		if(s.getMaxResults() != null) q.setMaxResults(s.getMaxResults());

		List<T> results =  q.getResultList();

		if(s.getEagerFetchProps()!= null)
			callGetters(results, s.getEagerFetchProps());

		return results;
	}

	/**
	 * @param search
	 * @return single result
	 * SELECTS ARE IGNORED!
	 */
	@Override
	public T searchOne(Search<T> s){

		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(daoType);
		Root<T> from = cq.from(daoType);
		cq.select(from);
		//distinct
		cq = cq.distinct(s.getDistinct());

		Map<String, Path> pathMap = new HashMap<>();
		//build the where clause
		if(s.getFilters() != null) {
			Predicate where = whereBuilder.build(cb, from, s.getFilters(), pathMap);
			cq.where(where);
		}

		//orderings
		if(s.getOrders() != null) {
			List<Order> orderings = JPAUtil.createOrderings(cb, from, s.getOrders(), pathMap);
			cq.orderBy(orderings);
		}

		TypedQuery<T> q = em.createQuery(cq);

		//hints
		if(s.getHints() != null)
			JPAUtil.setHints(q, from, s.getHints());

		//first and max results
		if(s.getFirstResult() != null) q.setFirstResult(s.getFirstResult());
		if(s.getMaxResults() != null) q.setMaxResults(s.getMaxResults());

		T result =  null;
		try {
			result = q.getSingleResult();
			if(s.getEagerFetchProps()!= null)
				callGetters(result, s.getEagerFetchProps());

		}
		catch (NoResultException e) {
		}
		catch(NonUniqueResultException e) {
		}


		return result;
	}

	@Override
	public <X> List<X> singleColumnSearch(Search<T> s){
		if(s.getSelects().size() > 1) {
			throw new UnsupportedOperationException("This method only accepts a single Select");
		}
		return search(s.getSelects().get(0), s);
	}
	
	/**
	 * Typed search method which returns a single column
	 * @param selects The single selection field
	 * @param search The search criteria - filters, orderings, etc. wrapped in a Search wrapper.
	 * @return
	 */
	@Override
	public <X> List<X> search(Select select, Search<T> s){

		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<X> cq = (CriteriaQuery<X>) cb.createQuery();
		Root<T> from = cq.from(daoType);
		//distinct
		cq = cq.distinct(s.getDistinct());

		Map<String, Path> pathMap = new HashMap<>();
		//selection
		if(select.getExpression()==null)
			throw new RuntimeException("Select expression has not been set.");
		Selection selection = JPAUtil.getSelections(cb, from, Arrays.asList(select), pathMap).get(0);
		cq.select(selection);

		//build the where clause
		if(s.getFilters() != null) {
			Predicate where = whereBuilder.build(cb, from, s.getFilters(), pathMap);
			cq.where(where);
		}

		//groupings
		if(s.getGroups() != null)
			cq.groupBy(JPAUtil.buildExpressions(cb, from, s.getGroups(), pathMap));

		//orderings
		if(s.getOrders() != null) {
			List<Order> orderings = JPAUtil.createOrderings(cb, from, s.getOrders(), pathMap);
			cq.orderBy(orderings);
		}

		TypedQuery<X> q = em.createQuery(cq);

		//hints
		if(s.getHints() != null)
			JPAUtil.setHints(q, from, s.getHints());

		//first and max results
		if(s.getFirstResult() != null) q.setFirstResult(s.getFirstResult());
		if(s.getMaxResults() != null) q.setMaxResults(s.getMaxResults());

		List<X> results =  q.getResultList();

		if(s.getEagerFetchProps()!= null)
			callGetters(results, s.getEagerFetchProps());

		return results;

	}

	@Override
	public List<Tuple> selectedSearch(Search<T> search){
		return search(search.getSelects(), search);
	}
	
	@Override
	public Report report(Search<T> search) {
		List<Tuple> results = selectedSearch(search);
		Report report = new Report(search.getTitle());
		addResultsToRowHolder(report, results, search);
		return report;
	}
	
	public void addResultsToRowHolder(RowHolder rh, List<Tuple> results, Search<T> search) {
		for(Tuple t: results) {
			ReportRow rr = new ReportRow();
			rh.addRow(rr);
			for(int i = 0; i < t.getElements().size(); i++) {
				Object o = t.get(i);
				String alias = search.getSelects().get(i).getDisplay();
				rr.addCell(new ReportCell(alias, o));
			}
		}
	}

	/**
	 * Search method which returns multiple columns in a Tuple object
	 * @param selects The ordered selection fields
	 * @param search The search criteria - filters, orderings, etc. wrapped in a Search wrapper.
	 * @return
	 */
	@Override
	public List<Tuple> search(List<Select> selects, Search<T> s){

		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<T> from = cq.from(daoType);
		//distinct
		cq = cq.distinct(s.getDistinct());

		Map<String, Path> pathMap = new HashMap<>();

		//selections
		if(selects == null || selects.size() == 0)
			throw new RuntimeException("Selects are required for this method.");
		List<Selection<?>> selections = JPAUtil.getSelections(cb, from, selects, pathMap);
		cq.multiselect(selections);

		//build the where clause
		if(s.getFilters() != null) {
			Predicate where = whereBuilder.build(cb, from, s.getFilters(), pathMap);
			cq.where(where);
		}

		//groupings
		if(s.getGroups() != null)
			cq.groupBy(JPAUtil.buildExpressions(cb, from, s.getGroups(), pathMap));

		//orderings
		if(s.getOrders() != null) {
			List<Order> orderings = JPAUtil.createOrderings(cb, from, s.getOrders(), pathMap);
			cq.orderBy(orderings);
		}

		TypedQuery<Tuple> q = em.createQuery(cq);

		//hints
		if(s.getHints() != null)
			JPAUtil.setHints(q, from, s.getHints());

		//first and max results
		if(s.getFirstResult() != null) q.setFirstResult(s.getFirstResult());
		if(s.getMaxResults() != null) q.setMaxResults(s.getMaxResults());

		List<Tuple> results =  q.getResultList();

		if(s.getEagerFetchProps()!= null)
			callGetters(results, s.getEagerFetchProps());

		return results;

	}

	/**
	 * Search method which returns multiple columns in a Tuple object
	 * @param selects An array of property Strings
	 * @param search The search criteria - filters, orderings, etc. wrapped in a Search wrapper.
	 * @return
	 */
	@Override
	public List<Tuple> search(String[] selections, Search<T> search) {
		List<Select> selects = new ArrayList<>();
		for(String s: selections)
			selects.add(new Select(s));
		return search(selects, search);
	}

	/**
	 * Counts the parameterized object type of the DAO class matching the filters
	 * @param filters
	 * @return
	 */
	@Override
	public Integer count(Collection<Filter<T>> filters) {
		return count(filters, false);
	}
	@Override
	public Integer count(Filter<T>... filters) {
		return count(Arrays.asList(filters), false);
	}
	/**
	 * Counts the parameterized object type of the DAO class matching the filters
	 * and distinct setting from the Search object
	 * @param search
	 * @return
	 */
	@Override
	public Integer count(Search<T> s) {
		return count(s.getFilters(), s.getDistinct());
	}
	/**
	 * Counts the parameterized object type of the DAO class matching the filters
	 * and distinct
	 * @param filters
	 * @param distinct
	 * @return
	 */
	@Override
	public Integer count(Collection<Filter<T>> filters, boolean distict){

		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> from = cq.from(daoType);
		cq.distinct(distict);
		cq.select(cb.count(from));

		Map<String, Path> pathMap = new HashMap<>();
		//build the where clause from the filters
		Predicate where = whereBuilder.build(cb, from, filters, pathMap);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();
	}


	/*
	 * Returns a List of T objects where input field is the min or max, with optional where clause
	 * Makes use of a subquery.
	 */
	@Override
	public List<T> searchByMinMaxField(String field, boolean max, Search<T> search) {
		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(daoType);
		Root<T> from = cq.from(daoType);
		cq.select(from);

		Map<String, Path> pathMap = new HashMap<>();
		//subquery
		Subquery sq = minMaxSubQuery(cb, cq, field, max, search);
		Expression selection1 = JPAUtil.makePath(field, from, pathMap);
		Predicate where = selection1.in(sq);

		//add optional filters to the where clause
		if(search != null && search.getFilters() != null)
			where = cb.and(where, whereBuilder.build(cb, from,  search.getFilters(), pathMap));
		cq.where(where);

		//add optional orderings
		if(search != null && search.getOrders() != null) {
			List<Order> orderings = JPAUtil.createOrderings(cb, from, search.getOrders(), pathMap);
			cq.orderBy(orderings);
		}

		TypedQuery<T> q = em.createQuery(cq);
		List<T> results = q.getResultList();
		return results;
	}


	@Override
	public List<T> searchByMaxField(String field, Search<T> search){
		return searchByMinMaxField(field, true, search);
	}

	@Override
	public List<T> searchByMinField(String field, Search<T> search){
		return searchByMinMaxField(field, false, search);
	}

	/*
	 * Subquery generator for the above method. Filters are applied to both the outer and subquery.
	 */
	private Subquery minMaxSubQuery(CriteriaBuilder cb, CriteriaQuery<T> cq, String field, boolean max, Search<T> search) {
		Subquery sq = cq.subquery(Comparable.class);
		Root<T> from = sq.from(daoType);
		Map<String, Path> pathMap = new HashMap<>();
		Expression path = JPAUtil.makePath(field, from, pathMap);

		//select the min/max of the path
		if(max) sq.select(cb.greatest(path));
		else sq.select(cb.least(path));

		//optional where clause from the filters
		if(search != null && search.getFilters() != null) {
			Predicate where = whereBuilder.build(cb, from, search.getFilters(), pathMap);
			sq.where(where);
		}
		return sq;
	}

	/*
	 * Returns the min/max/avg/count/sum of a single field, with optional where clause
	 * Replaces the getMinMaxValue method to support avg, count, and sum also.
	 */
	@Override
	public Object getAggregateValue(Select.Aggregate type, String field, Collection<Filter<T>> filters) {
		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<T> from = cq.from(daoType);
		Map<String, Path> pathMap = new HashMap<>();
		Expression path = JPAUtil.makePath(field, from, pathMap);


		if(type==Aggregate.MAX) cq.select(cb.greatest(path));
		else if(type==Aggregate.MIN) cq.select(cb.least(path));
		else if(type==Aggregate.AVG) cq.select(cb.avg(path));
		else if(type==Aggregate.COUNT) cq.select(cb.count(path));
		else if(type==Aggregate.SUM) cq.select(cb.sum(path));

		//build the where clause from the filters
		if(filters != null) {
			Predicate where = whereBuilder.build(cb, from, filters, pathMap);
			cq.where(where);
		}
		return em.createQuery(cq).getSingleResult();
	}


	/**
	 * Varargs version of the other getAggregateValue method
	 */
	@Override
	public Object getAggregateValue(Select.Aggregate type, String field, Filter<T>... filters) {
		return getAggregateValue(type, field, Arrays.asList(filters));
	}

	/**
	 *  SELECTs, GROUPs BY, and ORDERs BY the input selects.
	 *  Adds a COUNT to the SELECTs. Restricts results using the filters.
	 *  Returns a List of Object[] where each Object[] is the selected values plus the count.
	 */
	@Override
	public List<Object[]>  getCounts(List<Select> selects, List<Filter<T>> filters){
		assert selects!=null;
		EntityManager em = getEm();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<T> from = cq.from(daoType);
		Map<String, Path> pathMap = new HashMap<>();
		//selections
		List<Selection<?>> groups = JPAUtil.getSelections(cb, from, selects, pathMap);
		List<Selection<?>> selections = new ArrayList<>(groups);
		selections.add(cb.count(cb.literal(1)));
		//		selections.add(cb.count((Expression)selections.get(0)));
		cq.multiselect(selections);

		//group by
		cq.groupBy(groups);

		//build the where clause
		if(filters != null) {
			Predicate where = whereBuilder.build(cb, from, filters, pathMap);
			cq.where(where);
		}

		//orderings
		List<Order> orderings = JPAUtil.createOrderings(cb, from, makeAscOrdersFromSelects(selects), pathMap);
		cq.orderBy(orderings);

		TypedQuery tq = em.createQuery(cq);
		List results = tq.getResultList();
		return results;
	}

	@Override
	public List<OrderBy<T>> makeAscOrders(List<String> params){
		List<OrderBy<T>> out = new ArrayList<>();
		for(String s: params)
			out.add(new OrderBy<T>(s));
		return out;
	}

	@Override
	public List<OrderBy<T>> makeAscOrdersFromSelects(List<Select> selects){
		List<OrderBy<T>> out = new ArrayList<>();
		for(Select s: selects)
			out.add(new OrderBy<T>(s.getExpression()));
		return out;
	}

	/**
	 * Returns a List of Maps. 
	 * Each Map contains keys for each of the selected field's name mapped to the column value,
	 * plus one entry for the 'count' value for that combination.
	 */
	@Override
	public List<Map<String, Object>> getCountsAsMap(List<Select> fields, List<Filter<T>> filters){
		List<Object[]> results = getCounts(fields, filters);
		List<Map<String,Object>> out = new ArrayList<>();
		for(Object[] o: results) {
			Map<String, Object> m = new HashMap<>();
			for(int i = 0; i < fields.size(); i++) {
				Select s = fields.get(i);
				m.put(s.getDisplay(), o[i]);
			}
			m.put("count", o[fields.size()]);
			out.add(m);
		}
		return out;
	}

	@Override
	public List<Map<String, Object>> getCountsAsMapWithColumns(
			List<Select> fields,
			List<Filter<T>> baseFilters,
			Map<String, List<Filter<T>>> columnFilters,
			boolean grandTotalRow){
		//get every combination of the selection params, with total count
		List<Object[]> results = getCounts(fields, baseFilters);
		List<Map<String,Object>> out = new ArrayList<>();
		//make grand total row
		Map<String, Object> grandTotals = new HashMap<>();
		if(grandTotalRow) {
			grandTotals.put("total", 0L);
			for(String colKey: columnFilters.keySet())
				grandTotals.put(colKey, 0L);
		}

		//build the output rows. Query each combination of params for each column criteria
		for(Object[] o: results) {
			Map<String, Object> m = new HashMap<>();
			//new filter list = base filters plus the param values for this row
			List<Filter<T>> subfilters = new ArrayList<>(baseFilters);
			for(int i = 0; i < fields.size(); i++) {
				String s = fields.get(i).getDisplay();
				//add each param value to the map for the row
				m.put(s, o[i]);
				//Add each param = value to the subfilters for the column queries
				subfilters.add(new Filter<T>(fields.get(i).getExpression(), o[i]));
			}
			//add total count without subfilters
			Long total = (Long)o[fields.size()];
			m.put("total", total);
			if(grandTotalRow) {
				//update grand total column
				Long grandTotal = (Long)grandTotals.get("total");
				grandTotals.put("total", grandTotal + total);
			}

			//Column counts add additional filters
			for(String colKey: columnFilters.keySet()) {
				List<Filter<T>> colsubfilters = new ArrayList<>(subfilters);
				colsubfilters.addAll(columnFilters.get(colKey));
				List<Object[]> colresults = getCounts(fields, colsubfilters);//should only return a single result
				//why? because every grouping param has a set value added to the where clause
				if(colresults.size() > 0) {
					Long colCount = (Long)colresults.get(0)[fields.size()];
					m.put(colKey, colCount);
					if(grandTotalRow) {
						//update grand total column
						Long grandTotalCol = (Long)grandTotals.get(colKey);
						grandTotals.put(colKey, grandTotalCol + colCount);
					}
				}
				else m.put(colKey, 0);
			}
			out.add(m);
		}
		if(grandTotalRow)
			out.add(grandTotals);

		return out;
	}


	@Override
	public List<Count> getCountsAsTree(String... fields){
		return getCountsAsTree(makeSelects(fields), new ArrayList<>());
	}

	@Override
	public List<Count> getCountsAsTree(List<Select> fields){
		return getCountsAsTree(fields, new ArrayList<>());
	}

	@Override
	public List<Count> getCountsAsTree(List<Select> fields, List<Filter<T>> filters) {
		List<Count> out = new ArrayList<>();
		if(filters == null) filters = new ArrayList<>();
		//Initialize the grand total with just the filters and no groupings.
		int total = count(filters);
		//start recursive method
		out.addAll(getCountsAsTree2(fields, filters, 0, total));
		return out;
	}

	/**
	 * Recursive part of {@link #getCountsAsTree(List, List)}
	 *
	 * @param fields
	 * @param filters
	 * @param index
	 * @param total Used to compute the percentage of each subcount,
	 * which is then passed in to the recursive method call as the total
	 * @return
	 */
	private List<Count> getCountsAsTree2(List<Select> fields, List<Filter<T>> filters, int index, long total){
		List<Count> out = new ArrayList<>();
		List<Map<String, Object>> retvals = getCountsAsMap(fields.subList(0, index + 1), filters);
		for(Map<String, Object> valmap: retvals) {
			Count count = new Count();
			long subtotal = (Long)valmap.get("count");
			count.setCount(subtotal);
			count.setTotal(total);
			count.setPercent((float)subtotal/total*100);
			count.setValues(valmap);
			count.getValues().remove("count");//dont need count in the values map since its on the object
			out.add(count);
			//when index is fields.size - 2, we've already used all the fields so stop
			if(index < fields.size() - 1) {
				//copy the filters to add a filter for this value
				List<Filter<T>> filters2 = new ArrayList<>(filters);
				Select nextfield = fields.get(index);
				filters2.add(new Filter<T>(nextfield.getExpression(), valmap.get(nextfield.getDisplay())));
				//recursion for subcounts
				List<Count> subcounts = getCountsAsTree2(fields, filters2, index + 1, subtotal);
				count.setSubcounts(subcounts);
			}
		}
		return out;
	}

	@Override
	public int deleteAll(Search<T> search) {
		int count = 0;
		List<T> results = search(search);
		for(T t: results)
			if(delete(t))
				count++;
		return count;
	}
	
	@Override
	public List<?> getDistinctExp(Exp exp, boolean sortAsc){
		return search()
				.select(new Select(exp))
				.distinct()
				.addOrderBy(new OrderBy<>(exp, sortAsc))
				.singleColumnSearch();
	}
	
	@Override
	public SearchResult<T> searchWithResultWrapper(Search<T> search){
		SearchResult<T> out = new SearchResult<>();
		out.setFirstResult(search.getFirstResult());
		out.setPageSize(search.getMaxResults());
		out.setResultType(daoType);
		out.setResults(this.search(search));
		out.setTotalCount(this.count(search));
		return out;
	}
	@Override
	public SearchResult<T> simpleSearchWithResultWrapper(SimpleSearch simpleSearch){
		Search<T> search = SimpleSearchConverter.convertIn(simpleSearch, daoType);
		return searchWithResultWrapper(search);
	}

}

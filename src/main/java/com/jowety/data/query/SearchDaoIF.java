package com.jowety.data.query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jowety.data.client.search.Report;
import com.jowety.data.client.search.SearchResult;
import com.jowety.data.client.search.SimpleSearch;
import com.jowety.data.dao.EntityDaoIF;

import jakarta.persistence.Tuple;

/**
 * @author Jon Tyree
 *
 * @param <T>
 */
public interface SearchDaoIF<T> extends EntityDaoIF<T>{


	/**
	 * Method to return a single matching result entity.
	 * Catches the NoResultException and returns null
	 * @param filters array
	 * @return
	 */
	public T findOne(Filter<T>... filters);

	/**
	 * Method to return a single matching result entity.
	 * Catches the NoResultException and returns null
	 * @param filters Collection
	 * @return
	 */
	public T findOne(Collection<Filter<T>> filters);

	/**
	 * Convenience method to create an empty Search object for this Dao type.
	 * @return new empty search object.
	 */
	public Search<T> search();


	/**
	 * Helper method to make a List of Select objects
	 * @param args varargs list of property name strings
	 * @return
	 */
	public List<Select> makeSelects(String...args);

	/**
	 * Makes ascending orderings from path strings
	 * @param params
	 * @return
	 */
	public List<OrderBy<T>> makeAscOrders(List<String> params);

	/**
	 * Make ascending orderings from Select objects
	 * @param selects
	 * @return
	 */
	public List<OrderBy<T>> makeAscOrdersFromSelects(List<Select> selects);

	public List<T> search(Search<T> search);

	/**
	 * Same as the search to return a List<T> but calls getSingleResult instead
	 * @param s
	 * @return
	 */
	public T searchOne(Search<T> s);

	/**
	 * Typed search method which returns a single column
	 * @param selects The single selection field
	 * @param search The search criteria - filters, orderings, etc. wrapped in a Search wrapper.
	 * @return
	 */
	public <X> List<X> search(Select select, Search<T> s);


	/**
	 * Search method which returns multiple columns in a Tuple object
	 * @param selects The ordered selection fields
	 * @param search The search criteria - filters, orderings, etc. wrapped in a Search wrapper.
	 * @return
	 */
	public List<Tuple> search(List<Select> selects, Search<T> search);

	/**
	 * Search method which returns multiple columns in a Tuple object
	 * @param selects An array of property Strings
	 * @param search The search criteria - filters, orderings, etc. wrapped in a Search wrapper.
	 * @return
	 */
	public List<Tuple> search(String[] selects, Search<T> search);

	/**
	 * Counts the parameterized object type of the DAO class matching the filters
	 * @param filters
	 * @return
	 */
	public Integer count(Collection<Filter<T>> filters);
	public Integer count(Filter<T>... filters);
	public Integer count(Collection<Filter<T>> filters, boolean distinct);
	public Integer count(Search<T> search);

	/**
	 * Returns a List of T where the input field name is the greatest or least value of all T's.
	 * You're probably always going to just want a single result with this query, but it has the
	 * potential of returning more than one.
	 * For example, use "createdDate" and true to find the most recently saved entity.
	 * Uses a subselect to find the min/max value.
	 * Filters are added to the WHERE clause of both the main query and sub query.
	 * Roughly translates as: select t from T t where t.field = (select MAX(t.field) from T t where [filters]) AND [filters]
	 *
	 * @param field The property name to search for the greatest or least value
	 * @param max True for greatest/max value, false for min/least value
	 * @param filters FilterBy collection for the where clause. Optional, may be null.
	 * @return
	 */
	public List<T> searchByMinMaxField(String field, boolean max, Search<T> search);
	public List<T> searchByMaxField(String field, Search<T> search);
	public List<T> searchByMinField(String field, Search<T> search);

	/**
	 * Returns the min/max/avg/count/sum of a single field, with optional where clause
	 * @param type The type of aggregate
	 * @param field The name of the object field to aggregate
	 * @param filters Optional filters to generate a where clause
	 * @return
	 */
	public Object getAggregateValue(Select.Aggregate type, String field, Collection<Filter<T>> filters);

	/**
	 * Returns the min/max/avg/count/sum of a single field, with optional where clause
	 * @param type
	 * @param field
	 * @param filters
	 * @return
	 */
	public Object getAggregateValue(Select.Aggregate type, String field, Filter<T>... filters);

	/**
	 * Does a select plus count, group by, and order by for each field in the input params.
	 * Returns a row for each combination of selected fields plus the count.
	 * Example: You have an Employee Entity with gender and department fields.
	 * To get a count for each combination, just include 'gender' and 'department' in
	 * the fields List. Each Object[] in the results will contain
	 * [gender value, department name, count] such as
	 * ['female', 'accounting', 8]
	 *
	 * @param fields
	 * @param filters Optional for a where clause. Set null or empty list for no filtering.
	 * @return Each selected field value, plus the count for that unique combination.
	 * So, each Object[] returned will be fields.size + 1
	 */
	public List<Object[]>  getCounts(List<Select> selects, List<Filter<T>> filters);

	/**
	 * Returns the same results as {@link #getCounts(List, List)}, but each Object[]
	 * is packaged into a Map, where the input field name is the key, and the returned
	 * value is the value. For the count in each row, the Map key is "count".
	 * This way the values can be pulled out by the field name instead of by index.
	 * To continue the Employee example above: each returned Map in the List would
	 * have a result such as:
	 * ['gender':'female'; 'department':'accounting'; 'count':8]
	 *
	 * @param fields
	 * @param filters
	 * @return
	 */
	public List<Map<String, Object>> getCountsAsMap(List<Select> fields, List<Filter<T>> filters);


	/**
	 * The idea here is to generate a tabular report of counts where we want to group by one
	 * or more fields, then have some columns of counts next to those field values, with the row
	 * total on the end.
	 *
	 * <p>For example, say we want to generate a report listing each Employee department, with counts
	 * dividing the employees in that department based on some criteria, such as number of years.
	 * The fields parameter would only contain a Select with the 'department' path.
	 * The baseFilters are optional, if we want to filter the entire dataset by some criteria.
	 * The columnFilters Map would contain an entry for each column of employee counts, such as
	 * "< 1", "1-5", "5+" with filters for each to constrain the results by number of years.
	 *
	 * <p>The method will first call getCounts(fields, baseFilters) to get all the department names
	 * and the total count of Employees in each department based on the base filters. Then, for each
	 * department, it will call getCounts using the same fields but adding the department value plus
	 * the column filters for each column in turn. This will return a single count for each column which
	 * is a subcount to each row total. It's up to the user to generate column filters that make
	 * counts which add up to the total. For example, if you only have "<1" and "1-5" columns, you
	 * would exclude any Employees with 5+ years, so the numbers may or may not add up.
	 *
	 * @param fields The groupings which will generate the rows. For each field, a column will be generated.
	 * @param baseFilters Optional base criteria for the entire dataset.
	 * @param columnFilters Criteria for each column to generate a subcount.
	 * @param grandTotalRow Set TRUE to include a grand total row at the end which sums up each column.
	 * @return Each row in the report is a List entry. Each column is a Map entry,
	 * keyed either by the Select displayName for the fields, or the columnFilter Map key. Each row Map
	 * also contains a 'total' entry.
	 */
	public List<Map<String, Object>> getCountsAsMapWithColumns(
			List<Select> fields,
			List<Filter<T>> baseFilters,
			Map<String, List<Filter<T>>> columnFilters,
			boolean grandTotalRow);



	/**
	 * Similar to as {@link #getCountsAsMap(List, List)}, but produces a nested
	 * tree structure of {@link Count} objects instead of a flat List of Maps
	 * where each field is added successively to the grouping in a drill-down manner.
	 * For example, if we start with department,
	 * the first layer of Counts will be the Employee counts for each department.
	 * If gender is next, we'll then get nested subcounts for males and females
	 * within each department Count.
	 * We also add a percentage and total to each count.
	 * @param fields
	 * @param filters Optional. May be null.
	 * @return
	 * @see Count
	 */
	public List<Count> getCountsAsTree(List<Select> fields, List<Filter<T>> filters);
	/**
	 * Same as {@link #getCountsAsTree(List, List)} with no filters
	 * @param fields
	 * @return
	 */
	public List<Count> getCountsAsTree(List<Select> fields);
	/**
	 * Same as {@link #getCountsAsTree(List, List)} with no filters
	 * @param fields
	 * @return
	 */
	List<Count> getCountsAsTree(String... fields);


	public int deleteAll(Search<T> search) ;

	List<Tuple> selectedSearch(Search<T> search);

	Report report(Search<T> search);

	<X> List<X> singleColumnSearch(Search<T> s);

	/**
	 * Shortcut method for select distinct expression from T order by expression asc/desc
	 * @param exp The expression to get
	 * @param sortAsc ascending sort
	 * @return a list of a single value
	 */
	List<?> getDistinctExp(Exp exp, boolean sortAsc);

	SearchResult<T> simpleSearchWithResultWrapper(SimpleSearch simpleSearch);

	SearchResult<T> searchWithResultWrapper(Search<T> search);

}

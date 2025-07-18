package com.jowety.data.query.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jowety.data.query.Exp;
import com.jowety.data.query.Exp.ExpType;
import com.jowety.data.query.Function;
import com.jowety.data.query.Hint;
import com.jowety.data.query.OrderBy;
import com.jowety.data.query.Select;
import com.jowety.data.query.Select.Aggregate;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

/**
 * Utility methods for use with the JPA Helper
 * @author Jon.Tyree
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class JPAUtil {

	/**
	 * This is for addressing nested objects in expressions
	 * separated with '.'
	 * @param pathvals
	 * @param root
	 * @param pathMap A way to cache and reuse any created Path objects. This is necessary.
	 * Otherwise multiple filters that require the same join will end up creating multiple joins,
	 * which can produce duplicate rows in the query resultset.
	 * @return
	 */
	public static <X> Path<?> makePath(String[] pathvals, Root<X> root, Map<String, Path> pathMap){
		Path<?> path = pathMap.get(pathvals[0]);
		if(path == null) {
			path = root.get(pathvals[0]);
			pathMap.put(pathvals[0], path);
		}
		if(pathvals.length > 1)
			for(int i = 1; i < pathvals.length; i++) {
				String pathId = getPathString(pathvals, i);
				Path nextpath = pathMap.get(pathId);
				if(nextpath==null) {
					nextpath = path.get(pathvals[i]);
					pathMap.put(pathId, nextpath);
				}
				path = nextpath;
			}
		return path;
	}

	public static String getPathString(String[] pathvals, int maxindex) {
		StringBuilder out = new StringBuilder();
		for(int i = 0; i <= maxindex; i++) {
			out.append(pathvals[i]);
			if(i < maxindex) out.append('.');
		}
		return out.toString();
	}

	public static <X> Path<?> makePath(String fullPath, Root<X> root, Map<String, Path> pathMap){
		String[] path = makePathValues(fullPath);
		return makePath(path, root, pathMap);
	}

	public static String[] makePathValues(String path) {
		if(path.indexOf(".") > -1) return path.split("\\.");
		else return new String[] {path};
	}

	public static <X> Path makeOuterJoinPath(String[] pathvals, Root<X> root) {
		Path path = root.join(pathvals[0], JoinType.LEFT);
		if(pathvals.length > 1)
			for(int i = 1; i < pathvals.length; i++)
				path = path.get(pathvals[i]);
		return path;
	}

	/**
	 * A Function can have multiple arguments, each of which can be literal, path, or another Function.
	 * Calls {@link #buildExpressions(CriteriaBuilder, Root, List, Map)} to create the Expression[].
	 * @param f
	 * @param root
	 * @param cb
	 * @param pathMap
	 * @return
	 */
	public static <X> Expression getFunctionExpression(Function f, Root<X> root, CriteriaBuilder cb, Map<String, Path> pathMap) {
		List<Expression<?>> args = buildExpressions(cb, root, f.getArgs(), pathMap);
		return cb.function(f.getName(), f.getType(), args.toArray(new Expression[args.size()]));
	}

	public static <X> List<Expression<?>> buildExpressions(CriteriaBuilder cb, Root<X> root, List<Exp> args, Map<String, Path> pathMap) {
		List<Expression<?>> out = new ArrayList<Expression<?>>();
		for(Exp exp: args)
			out.add(buildExpression(cb, root, exp, pathMap));
		return out;
	}
	/**
	 * Turns an Exp object from the helper framework into a jakarta.persistence.criteria.Expression
	 * @param cb
	 * @param root
	 * @param exp
	 * @param pathMap
	 * @return
	 */
	public static <X> Expression buildExpression(CriteriaBuilder cb, Root<X> root, Exp exp, Map<String, Path> pathMap) {
		Expression out = null;
		if(exp.getType()==ExpType.LITERAL) {
			if(exp.getValue()==null) out = cb.nullLiteral(Object.class);
			else out = cb.literal(exp.getValue());
		}

		else if(exp.getType()==ExpType.PATH)
			out = makePath((String)exp.getValue(), root, pathMap);

		else if(exp.getType()==ExpType.FUNCTION)
			out = getFunctionExpression((Function)exp.getValue(), root, cb, pathMap);

		return out;
	}

	public static <X> List<Expression<?>> makePaths(CriteriaBuilder cb, Root<X> root, List<String> values, Map<String, Path> pathMap){
		List<Expression<?>> out = new ArrayList<Expression<?>>();
		for(String s: values)
			out.add(makePath(s, root, pathMap));
		return out;
	}

	/**
	 * Takes the JPA Helper Select objects and turns them into jakarta.persistence.criteria.Selection objects,
	 * blowing out any paths with dot notation.
	 * @param cb
	 * @param root
	 * @param selects
	 * @return
	 */
	public static <X> List<Selection<?>> getSelections(CriteriaBuilder cb, Root<X> root, List<Select> selects, Map<String, Path> pathMap){
		List<Selection<?>> paths = new ArrayList<Selection<?>>();
		for(Select s: selects) {
			Selection<?> path = null;
			if(s.getAggregate()!=null && s.getExpression().getType() == ExpType.PATH)
				path = buildAggregateExpression(cb, root, s.getExpression(), s.getAggregate(), pathMap);

			else path = buildExpression(cb, root, s.getExpression(), pathMap);
			paths.add(path);
		}
		return paths;
	}

	public static <X> Expression buildAggregateExpression(CriteriaBuilder cb, Root<X> root, Exp exp, Aggregate type, Map<String, Path> pathMap) {
		Expression path = buildExpression(cb, root, exp, pathMap);
		if(type==Aggregate.MAX) return cb.greatest(path);
		else if(type==Aggregate.MIN) return cb.least(path);
		else if(type==Aggregate.AVG) return cb.avg(path);
		else if(type==Aggregate.COUNT) return cb.count(path);
		else if(type==Aggregate.SUM) return cb.sum(path);
		return null;
	}

	/**
	 * Takes the JPA Helper OrderBy objects and makes jakarta.persistence.criteria.Order objects
	 * blowing out any paths with dot notation.
	 * @param cb
	 * @param root
	 * @param orders
	 * @return
	 */
	public static <X> List<Order> createOrderings(CriteriaBuilder cb, Root<X> root, List<OrderBy<X>> orders, Map<String, Path> pathMap){

		List<Order> out = new ArrayList<Order>();

		for(OrderBy<X> o: orders) {
			Expression x = buildExpression(cb, root, o.getExpression(), pathMap);

			//					Path path = o.isOuterjoin()?
			//							makeOuterJoinPath(pathvals, root):
			//								makePath(pathvals, root, pathMap);

			Order order = o.isAscending()?
					cb.asc(x):
						cb.desc(x);

					out.add(order);
		}
		return out;
	}

	/**
	 * Sets the hints on the Query prepending the root alias to the value if necessary
	 * @param q
	 * @param r
	 * @param hints
	 */
	public static void setHints(Query q, Root<?> r, Collection<Hint> hints) {
		for(Hint h: hints) {
			Object value = h.getPrependRootAlias()? r.getAlias() + "." + h.getValue(): h.getValue();
			q.setHint(h.getName(), value);
		}
	}

	public static List<Select> makeSelects(String...args){
		List<Select> selects = new ArrayList<Select>();
		for(String s: args)
			selects.add(new Select(s));
		return selects;
	}
}

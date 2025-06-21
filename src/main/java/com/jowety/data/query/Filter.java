/**
 *
 */
package com.jowety.data.query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * Used with the DAOBase search and count methods.
 * @author Jon.Tyree
 *
 */

/**
 * @author tororojo
 *
 * @param <T>
 */
public class Filter<T>  implements Serializable{

	private static final long serialVersionUID = 9216625708711540226L;
	private static Map<String, MatchMode> matchModeMap = new HashMap<>();

	public static enum MatchMode{

		/**
		 * Requires value type of Object
		 */
		EQUALS (false, "=", "eq"),
		/**
		 * Requires value type of Object
		 */
		NOT_EQUAL (false, "ne", "!=", "notequals"),
		/**
		 * Specialized LIKE. Creates a value plus wildcard restriction.
		 * Requires value type of String
		 */
		STARTSWITH (false, "starts"),
		/**
		 * Case insensitive STARTSWITH. Creates a value plus wildcard restriction LIKE.
		 * Upper cases the match string and the database value.
		 * Requires value type of String
		 */
		STARTSWITH_CI (false, "starts_ci"),
		/**
		 * Specialized LIKE. Creates a value plus wildcard restriction.
		 * Requires value type of String
		 */
		ENDSWITH (false, "ends"),
		/**
		 * Case insensitive ENDSWITH. Creates a value plus wildcard restriction.
		 * Upper cases the match string and the database value.
		 * Requires value type of String
		 */
		ENDSWITH_CI (false, "ends_ci"),
		/**
		 * Specialized LIKE.  Wraps the value with wildcards on both sides.
		 * Requires value type of String
		 */
		CONTAINS (false),
		/**
		 * Specialized NOT LIKE.  Wraps the value with wildcards on both sides.
		 * Requires value type of String
		 */
		DOESNT_CONTAIN (false, "notcontains"),
		/**
		 * Case insensitive CONTAINS. Wraps the value with wildcards on both sides.
		 * Upper cases the match string and the database value.
		 * Requires value type of String
		 */
		CONTAINS_CI (false),
		/**
		 * Case insensitive NEGATIVE CONTAINS. Wraps the value with wildcards on both sides.
		 * Upper cases the match string and the database value.
		 * Requires value type of String
		 */
		DOESNT_CONTAIN_CI (false),
		/**
		 * Put your own wildcards in the value string.
		 * Requires value type of String.
		 */
		LIKE (false),
		/**
		 * Case insensitive. Upper cases the match string and the database value.
		 * Put your own wildcards in the value string.
		 * Requires value type of String.
		 */
		LIKE_CI (false),
		/**
		 * Put your own wildcards in the value string.
		 * Requires value type of String.
		 */
		NOT_LIKE (false),
		/**
		 * Put your own wildcards in the value string.
		 * Requires value type of String.
		 */
		NOT_LIKE_CI (false),
		/**
		 * Requires value type of java.util.Collection
		 */
		IN (false),
		/**
		 * Requires value type of java.util.Collection
		 */
		NOT_IN (false),
		/**
		 * Creates a restriction that the path value IS NULL.
		 * No value required, only path.
		 */
		NULL (false),
		/**
		 * Creates a restriction that the path value IS NOT NULL.
		 * No value required, only path.
		 */
		NOT_NULL (false),
		/**
		 * Requires a value type of java.util.Date.
		 * Creates a restriction that the path value is on the same day.
		 */
		ON_DATE(false),
		/**
		 * Requires a value type of java.lang.Comparable
		 */
		GREATER_THAN(false, "gt", ">"),
		/**
		 * Requires a value type of java.lang.Comparable
		 */
		GREATER_THAN_OR_EQUAL(false, "gte", ">="),
		/**
		 * Requires a value type of java.lang.Comparable
		 */
		LESS_THAN(false, "lt", "<"),
		/**
		 * Requires a value type of java.lang.Comparable
		 */
		LESS_THAN_OR_EQUAL(false, "lte", "<="),


		/**
		 * Requires value type of Collection<FilterBy>, no path required
		 */
		OR (true),
		/**
		 * Requires value type of Collection<FilterBy>, no path required
		 */
		AND (true);

		boolean gouping;
		

		private MatchMode(boolean gouping, String... alias) {
			this.gouping = gouping;
			matchModeMap.put(this.name().toLowerCase(), this);
			for(String a: alias) {
				matchModeMap.put(a, this);
			}
		}

		public boolean isGouping() {
			return gouping;
		}

	};
	
	public static MatchMode getMatchModeByAlias(String alias) {
		return matchModeMap.get(alias.toLowerCase());
	}

	/**
	 * Each FilterBy holds an expression (Exp) which is either a path, a function, or a literal value.
	 * Expression is ignored for AND/OR match modes.
	 */
	private Exp leftside;
	private Exp rightside;
	private boolean negative = false;
	private MatchMode match = MatchMode.EQUALS;


	public Filter() {
	}

	/**
	 * Default MatchMode is EQUALS
	 * @param path The property path on the query object. Nested paths are supported with dot notation (ex. "address.state")
	 * @param value The concrete value(s) to restrict the property to. Object type depends on the MatchMode.
	 */
	public Filter(String path, Object value) {
		super();
		this.leftside = Exp.path(path);
		this.rightside = value instanceof Exp? (Exp)value: Exp.literal(value);
	}

	public Filter(Function func, Object value) {
		super();
		this.leftside = Exp.function(func);
		this.rightside = value instanceof Exp? (Exp)value: Exp.literal(value);
	}

	public Filter(Exp exp, Object value) {
		super();
		this.leftside = exp;
		this.rightside = value instanceof Exp? (Exp)value: Exp.literal(value);
	}


	/**
	 * @param path The property path on the query object. Nested paths are supported with dot notation (ex. "address.state")
	 * @param value The concrete value(s) to restrict the property to. Object type depends on the MatchMode.
	 * @param match The type of comparison to use
	 */
	public Filter(String path, Object value, MatchMode match) {
		super();
		this.leftside = Exp.path(path);
		if(value instanceof Exp) {
			this.rightside = (Exp)value;
		} else if(value instanceof Function) {
			this.rightside = Exp.function((Function)value);
		} else {
			this.rightside = Exp.literal(value);
		}
		this.match = match;
	}

	public Filter(Function func, Object value, MatchMode match) {
		super();
		this.leftside = Exp.function(func);
		if(value instanceof Exp) {
			this.rightside = (Exp)value;
		} else if(value instanceof Function) {
			this.rightside = Exp.function((Function)value);
		} else {
			this.rightside = Exp.literal(value);
		}
		this.match = match;
	}
	public Filter(Exp exp, Object value, MatchMode match) {
		super();
		this.leftside = exp;
		if(value instanceof Exp) {
			this.rightside = (Exp)value;
		} else if(value instanceof Function) {
			this.rightside = Exp.function((Function)value);
		} else {
			this.rightside = Exp.literal(value);
		}
		this.match = match;
	}

	/**
	 * @param path The property path on the query object. Nested paths are supported with dot notation (ex. "address.state")
	 * @param value The concrete value(s) to restrict the property to. Object type depends on the MatchMode.
	 * @param match The type of comparison to use
	 * @param negative Negativity flag reverses the match
	 */
	public Filter(String path, Object value, MatchMode match, boolean negative) {
		super();
		this.leftside = Exp.path(path);
		if(value instanceof Exp) {
			this.rightside = (Exp)value;
		} else if(value instanceof Function) {
			this.rightside = Exp.function((Function)value);
		} else {
			this.rightside = Exp.literal(value);
		}
		this.match = match;
		this.negative = negative;
	}

	public Filter(Function func, Object value, MatchMode match, boolean negative) {
		super();
		this.leftside = Exp.function(func);
		if(value instanceof Exp) {
			this.rightside = (Exp)value;
		} else if(value instanceof Function) {
			this.rightside = Exp.function((Function)value);
		} else {
			this.rightside = Exp.literal(value);
		}
		this.match = match;
		this.negative = negative;
	}

	public Filter(Exp exp, Object value, MatchMode match, boolean negative) {
		super();
		this.leftside = exp;
		if(value instanceof Exp) {
			this.rightside = (Exp)value;
		} else if(value instanceof Function) {
			this.rightside = Exp.function((Function)value);
		} else {
			this.rightside = Exp.literal(value);
		}
		this.match = match;
		this.negative = negative;
	}


	/**
	 * This constructor can only be used with MatchMode.AND and MatchMode.OR
	 * to create a subgroup of conjuncted (AND) or disjuncted (OR) filters.
	 * AND/OR filters have NO LEFT SIDE!
	 * RIGHT SIDE is an EXP Literal whose value is a java.util.Collection of Filter objects
	 * @param value
	 * @param match
	 */
	public Filter(Collection<Filter<T>> value, MatchMode match) {
		super();
		this.rightside = Exp.literal(value);
		this.match = match;
		if(!(match==MatchMode.AND || match==MatchMode.OR))
			throw new RuntimeException("This constructor only applies to AND/OR filters");
	}


	/**
	 * @return the expression
	 */
	public Exp getLeftSide() {
		return leftside;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setLeftSide(Exp expression) {
		this.leftside = expression;
	}

	/**
	 * The value of the filter. Object type depends on the
	 * property being filtered as well as the MatchMode.
	 * The IN match mode requires a java.util.Collection value type.
	 * @return the value
	 */
	public Exp getRightSide() {
		return rightside;
	}

	/**
	 * The MatchMode
	 * @return the match
	 */
	public MatchMode getMatchMode() {
		return match;
	}


	/**
	 * @param value the value to set
	 */
	public void setRightSide(Exp expression) {
		this.rightside = expression;
	}

	/**
	 * @param negative the negative to set
	 */
	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	/**
	 * @param match the match to set
	 */
	public void setMatchMode(MatchMode match) {
		this.match = match;
	}

	/**
	 * If set TRUE, negates the expression.
	 * I.e. NOT EQUAL, NOT IN, NOT NULL, etc.
	 * @return the negative
	 */
	public boolean isNegative() {
		return negative;
	}


	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("exp", this.leftside)
				.append("value", this.rightside)
				.append("match", this.match)
				.append("negate", this.negative);
		return tsb.toString();
	}

	public static <X> Filter<X> andGroup(Filter<X>... filter){
		return new Filter<>(new HashSet<>(Arrays.asList(filter)), MatchMode.AND);
	}

	private transient Search<T> search;

	/**
	 * Package access. Used by Search.where() to start the Filter builder.
	 * @param search injected Search to be returned at the end of the builder.
	 * @return
	 */
	LeftSide startWhere(Search<T> search) {
		this.search = search;
		return new LeftSide();
	}

	/**
	 * Lets you specify the left side of the Filter by way of
	 * a Path, Function, or a literal value.
	 * All LeftSide methods return a Matching.
	 * @author Jon Tyree
	 *
	 */
	public class LeftSide{
		public Matching path(String path) {
			leftside = Exp.path(path);
			return new Matching();
		}
		public Matching literal(Object value) {
			leftside = Exp.literal(value);
			return new Matching();
		}
		public Matching function(Function func) {
			leftside = Exp.function(func);
			return new Matching();
		}
	}
	/**
	 * Specifies the MatchMode of the filter.
	 * All Matching methods return a RightSide (except null and notNull, which do
	 * not have a comparison value)
	 * @author Jon Tyree
	 *
	 */
	public class Matching{

		public RightSide equals(){
			match = MatchMode.EQUALS;
			return new RightSide();
		}
		public RightSide notEqual(){
			match = MatchMode.NOT_EQUAL;
			return new RightSide();
		}
		public RightSide startsWith(){
			match = MatchMode.STARTSWITH;
			return new RightSide();
		}
		public RightSide startsWithCI(){
			match = MatchMode.STARTSWITH_CI;
			return new RightSide();
		}
		public RightSide contains(){
			match = MatchMode.CONTAINS;
			return new RightSide();
		}
		public RightSide containsCI(){
			match = MatchMode.CONTAINS_CI;
			return new RightSide();
		}
		public RightSide doesntContain(){
			match = MatchMode.DOESNT_CONTAIN;
			return new RightSide();
		}
		public RightSide doesntContainCI(){
			match = MatchMode.DOESNT_CONTAIN_CI;
			return new RightSide();
		}
		public RightSide endsWith(){
			match = MatchMode.ENDSWITH;
			return new RightSide();
		}
		public RightSide endsWithCI(){
			match = MatchMode.ENDSWITH_CI;
			return new RightSide();
		}
		public RightSide like(){
			match = MatchMode.LIKE;
			return new RightSide();
		}
		public RightSide likeCI(){
			match = MatchMode.LIKE_CI;
			return new RightSide();
		}
		public RightSide notLike(){
			match = MatchMode.NOT_LIKE;
			return new RightSide();
		}
		public RightSide notLikeCI(){
			match = MatchMode.NOT_LIKE_CI;
			return new RightSide();
		}
		public Search<T> isNull(){
			match = MatchMode.NULL;
			return search;
		}
		public Search<T> notNull(){
			match = MatchMode.NOT_NULL;
			return search;
		}
		public RightSide greaterThan(){
			match = MatchMode.GREATER_THAN;
			return new RightSide();
		}
		public RightSide greaterOrEqual(){
			match = MatchMode.GREATER_THAN_OR_EQUAL;
			return new RightSide();
		}
		public RightSide lessThan(){
			match = MatchMode.LESS_THAN;
			return new RightSide();
		}
		public RightSide lessOrEqual(){
			match = MatchMode.LESS_THAN_OR_EQUAL;
			return new RightSide();
		}
		public RightSide in(){
			match = MatchMode.IN;
			return new RightSide();
		}
		public RightSide notIn(){
			match = MatchMode.NOT_IN;
			return new RightSide();
		}
	}
	/**
	 * Same as the LeftSide, but sets the comparison value of the Filter.
	 * All RightSide methods return back to the Search object.
	 * @author Jon Tyree
	 *
	 */
	public class RightSide{
		public Search<T> path(String path) {
			rightside = Exp.path(path);
			return search;
		}
		public Search<T> literal(Object value) {
			rightside = Exp.literal(value);
			return search;
		}
		public Search<T> function(Function func) {
			rightside = Exp.function(func);
			return search;
		}
	}
}

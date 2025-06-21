package com.jowety.data.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converts a client-search Search object (untyped) into a typed framework-query-core Search object.
 *
 */
public class SearchConverter {


	public static <T> Search<T> convertIn(com.jowety.data.client.search.Search b, Class<T> type){
		Search<T> out = new Search<>();
		//selects
		if(b.getSelects() != null) {
			for(com.jowety.data.client.search.Select sel: b.getSelects()) {
				out.select(convertSelect(sel));
			}
		}
		//Filters
		if(b.getFilters() != null) {
			for(com.jowety.data.client.search.Filter fb: b.getFilters()) {
				out.addFilter(convertFilter(fb, type));
			}
		}
		//orderBys
		if(b.getOrders() != null) {
			for(com.jowety.data.client.search.OrderBy ob: b.getOrders()) {
				OrderBy<T> obOut = new OrderBy<>(ob.getProperty(), ob.isAscending(), ob.isOuterjoin());
				out.addOrderBy(obOut);
			}
		}
		//groups
		if(b.getGroups() != null) {
			List<Exp> groups = new ArrayList<>();
			for(com.jowety.data.client.search.Exp expIn: b.getGroups()) {
				Exp expOut = convertExp(expIn);
				groups.add(expOut);
			}
			out.setGroups(groups);
		}
		out.setDistinct(b.getDistinct());
		out.setFirstResult(b.getFirstResult());
		out.setMaxResults(b.getMaxResults());
		return out;
	}

	public static <T> Select convertSelect(com.jowety.data.client.search.Select sel){
		Select selOut = new Select();
		selOut.setExpression(convertExp(sel.getExpression()));
		selOut.setDisplay(sel.getDisplay());
		if(sel.getAggregate() != null) {
			selOut.setAggregate(Select.Aggregate.valueOf(sel.getAggregate().name()));
		}
		return selOut;
	}

	public static <T> Filter<T> convertFilter(com.jowety.data.client.search.Filter fb, Class<T> type){
		Filter<T> fbOut = new Filter<>();
		fbOut.setMatchMode(Filter.MatchMode.valueOf(fb.getMatchMode().name()));
		fbOut.setNegative(fb.isNegative());
		//AND/OR filters have a Collection of FilterBys as their value, so those would need converted also
		if(fb.getMatchMode().isGouping()) {
			if(!Collection.class.isAssignableFrom(fb.getRightSide().getValue().getClass()))
				throw new RuntimeException(
						"Filter values for AND/OR MatchModes must be of type java.util.Collection");
			Collection<com.jowety.data.client.search.Filter> subs = (Collection<com.jowety.data.client.search.Filter>)fb.getRightSide().getValue();
			List<Filter<T>> newFilters = new ArrayList<>();
			for(com.jowety.data.client.search.Filter subFb: subs) {
				//recurse to convert each nested FilterBy
				newFilters.add(convertFilter(subFb, type));
			}
			//no left side for AND/OR filters
			//right side is Exp.literal holding the collection
			fbOut.setRightSide(Exp.literal(newFilters));
		}
		else {
			fbOut.setLeftSide(convertExp(fb.getLeftSide()));
			fbOut.setRightSide(convertExp(fb.getRightSide()));
		}		
		return fbOut;
	}

	public static Exp convertExp(com.jowety.data.client.search.Exp expIn) {
		Exp exp = new Exp();
		exp.setType(Exp.ExpType.valueOf(expIn.getType().name()));
		//Function type Expressions need to get converted
		if(expIn.getType() == com.jowety.data.client.search.Exp.ExpType.FUNCTION) {
			if(com.jowety.data.client.search.Function.class.isAssignableFrom(expIn.getFunction().getClass()) == false) {
				throw new RuntimeException("Exp is type FUNCTION but value is not an instance of com.jowety.data.client.search.Function");
			}
			Function f = convertFunction((com.jowety.data.client.search.Function)expIn.getFunction());
			exp.setFunction(f);
		}
		//Literal and Path types just copy the value over
		else if(expIn.getType() == com.jowety.data.client.search.Exp.ExpType.PATH) {
			exp.setPath(expIn.getPath());
		}		
		else if(expIn.getType() == com.jowety.data.client.search.Exp.ExpType.LITERAL) {
			exp.setLiteral(expIn.getLiteral());
		}
		return exp;
	}

	public static Function convertFunction(com.jowety.data.client.search.Function funcIn) {
		Function f = new Function();
		f.setName(funcIn.getName());
		f.setType(funcIn.getType());
		for(com.jowety.data.client.search.Exp expIn: funcIn.getArgs()) {
			f.getArgs().add(convertExp(expIn));
		}
		return f;
	}

}

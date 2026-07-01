package com.jowety.data.query;

import java.time.LocalDate;

import com.jowety.data.client.search.SimpleSearch;
import com.jowety.data.query.Filter.MatchMode;

public class SimpleSearchConverter {
	
	public static <T> Search<T> convertIn(SimpleSearch b, Class<T> type) {
		Search<T> out = new Search<>();
		// Filters
		if (b.getFilters() != null) {
			for (String fb : b.getFilters()) {
				out.addFilter(convertFilter(fb, type));
			}
		}
		// orderBys
		if (b.getOrders() != null) {
			for (String ob : b.getOrders()) {
				String[] parts = ob.trim().split(" ");
				boolean asc = true;
				if(parts.length == 2 && parts[1].equalsIgnoreCase("desc")) {
					asc = false;
				}
				OrderBy<T> obOut = new OrderBy<>(parts[0], asc);
				out.addOrderBy(obOut);
			}
		}
		out.setFirstResult(b.getFirstResult());
		out.setMaxResults(b.getMaxResults());
		return out;
	}

	public static <T> Filter<T> convertFilter(String f, Class<T> type) {
		Filter<T> fbOut = new Filter<>();
		String[] parts = f.trim().split(" ", 3);
		if (parts.length != 3) {
			throw new RuntimeException(
					"SimpleSearch filter values must be of format \"<path> <match operator> <literal value>\"");
		}
		fbOut.setLeftSide(Exp.path(parts[0]));
		MatchMode mm = null;
		if(f.contains("is null")) mm = MatchMode.NULL;
		else if(f.contains("not null")) mm = MatchMode.NOT_NULL;
		else {
			mm = Filter.getMatchModeByAlias(parts[1]);
			if (mm == null) {
				throw new RuntimeException("MatchMode not found for expression " + parts[1]);
			}
			Object value = getLiteralValue(parts[2]);
			fbOut.setRightSide(Exp.literal(value));
		}
		fbOut.setMatchMode(mm);
		return fbOut;
	}
	
	public static Object getLiteralValue(String input) {
		if(input.startsWith("'") && input.endsWith("'")) {
			return input.substring(1, input.length() - 1);
		}
		else if(input.matches("\\d+")) {
			return Long.valueOf(input);
		}
		else if(input.matches("\\d+(\\.\\d+)")) {
			return Double.valueOf(input);
		}
		else if(input.startsWith("localDate:")) {
			String val = input.substring(10);
			return LocalDate.parse(val);
		}
		else if(input.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}
		else if(input.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		}
		return input;
	}
}

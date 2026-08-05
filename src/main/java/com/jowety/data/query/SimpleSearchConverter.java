package com.jowety.data.query;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.jowety.data.client.search.SimpleSearch;
import com.jowety.data.query.Filter.MatchMode;

public class SimpleSearchConverter {
	
	private static final ConversionService conversionService = new DefaultFormattingConversionService();
	
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
		String propertyName = parts[0];
		fbOut.setLeftSide(Exp.path(propertyName));
		MatchMode mm = null;
		if(f.contains("is null")) mm = MatchMode.NULL;
		else if(f.contains("not null")) mm = MatchMode.NOT_NULL;
		else {
			mm = Filter.getMatchModeByAlias(parts[1]);
			if (mm == null) {
				throw new RuntimeException("MatchMode not found for expression " + parts[1]);
			}
			Object value = convert(type, propertyName, parts[2]);
			fbOut.setRightSide(Exp.literal(value));
		}
		fbOut.setMatchMode(mm);
		return fbOut;
	}
	
	/**
     * Statically converts a raw string value into the exact object type expected 
     * by a target class property.
     *
     * @param targetClass  The class owning the property (e.g., TransactionDto.class)
     * @param propertyName The name of the field (e.g., "strikePrice" or "type")
     * @param rawValue     The raw text filter string from your UI/API
     * @return The strongly-typed object (e.g., BigDecimal, LocalDate, Enum)
     */
    public static Object convert(Class<?> targetClass, String propertyName, String rawValue) {
        if (targetClass == null || propertyName == null) {
            throw new IllegalArgumentException("Class and property name must not be null");
        }
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(targetClass, propertyName);        
        if (pd == null) {
            throw new IllegalArgumentException(
                String.format("Property '%s' not found on class %s", propertyName, targetClass.getSimpleName())
            );
        }
        Class<?> targetType = pd.getPropertyType();
        if (rawValue == null || (rawValue.isEmpty() && targetType != String.class)) {
            return null;
        }
        return conversionService.convert(rawValue, targetType);
    }
	
	public static Object getLiteralValue(String input, Class<?> type) {
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

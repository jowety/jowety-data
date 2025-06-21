package com.jowety.data.query.function;

import com.jowety.data.query.Function;

/**
 * @author Jon.Tyree
 * TO_CHAR is an Oracle function. 
 * The path parameter can reference a Date, char or number field.
 * The optional format parameter should be an Oracle date/time/number format string
 * example: new FilterBy<Order>(
		new ToChar("fromDate", "YYYYMMDD"), value, MatchMode.STARTSWITH));
 */
public class ToChar extends Function {

	private static final long serialVersionUID = 1L;

	public ToChar(String path, String dateFormat) {
		super("TO_CHAR", String.class);
		pathArg(path).literalArg(dateFormat);
	}
	
	public ToChar(String path) {
		super("TO_CHAR", String.class);
		pathArg(path);
	}
}

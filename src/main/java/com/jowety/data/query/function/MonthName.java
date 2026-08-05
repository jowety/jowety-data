package com.jowety.data.query.function;

import com.jowety.data.query.Function;

/**
 * monthname function needs a date type input
 * returns a String
 * @author Jon.Tyree
 */
public class MonthName extends Function {

	private static final long serialVersionUID = 1L;

	public MonthName(String path) {
		super("MONTHNAME", String.class);
		pathArg(path);
	}
}

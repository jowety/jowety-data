package com.jowety.data.query.function;

import java.util.Date;

import com.jowety.data.query.Function;


/**
 * @author Jon.Tyree
 * TO_DATE is an Oracle function. 
 */
public class ToDate extends Function {

	private static final long serialVersionUID = 1L;

	public ToDate(String path, String dateFormat) {
		super("TO_DATE", Date.class);
		pathArg(path).literalArg(dateFormat);
	}
	
	public ToDate(String path) {
		super("TO_DATE", Date.class);
		pathArg(path);
	}
}

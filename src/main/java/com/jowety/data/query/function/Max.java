package com.jowety.data.query.function;

import com.jowety.data.query.Function;


public class Max extends Function {

	private static final long serialVersionUID = 1L;

	public Max(String path) {
		super("MAX", java.sql.Timestamp.class);
		pathArg(path);
	}
}

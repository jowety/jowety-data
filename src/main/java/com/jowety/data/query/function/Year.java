package com.jowety.data.query.function;

import com.jowety.data.query.Function;

/**
 * @author Jon.Tyree
 */
public class Year extends Function {

	private static final long serialVersionUID = 1L;

	public Year(String path) {
		super("YEAR", Integer.class);
		pathArg(path);
	}
}

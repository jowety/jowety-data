package com.jowety.data.query.function;

import com.jowety.data.query.Function;

/**
 * @author Jon.Tyree
 */
public class Month extends Function {

	private static final long serialVersionUID = 1L;

	public Month(String path) {
		super("MONTH", Integer.class);
		pathArg(path);
	}
}

package com.jowety.data.query.function;

import com.jowety.data.query.Function;

/**
 * @author Jon.Tyree
 * SUBSTR sql function
 * int position - the starting position in the string
 * int length - number of characters to extract (optional, if omitted takes the remaining chars in the string)
 * 
 */
public class SubStr extends Function {

	private static final long serialVersionUID = 1L;

	/**
	 * @param path
	 * @param position
	 */
	public SubStr(String path, int position) {
		super("SUBSTR", String.class);
		pathArg(path).literalArg(position + "");
	}

	/**
	 * @param path
	 * @param position
	 * @param length
	 */
	public SubStr(String path, int position, int length) {
		super("SUBSTR", String.class);
		pathArg(path).literalArg(position + "").literalArg(length + "");
	}
}

package com.jowety.data.query;

import com.jowety.data.query.function.Length;
import com.jowety.data.query.function.SubStr;
import com.jowety.data.query.function.ToChar;
import com.jowety.data.query.function.ToDate;
import com.jowety.data.query.function.Year;

public class Functions {
	
	public static Function year(String path) {
		return new Year(path);
	}
	
	public static Function length(String path) {
		return new Length(path);
	}
	
	public static Function subStr(String path, int position) {
		return new SubStr(path, position);
	}
	
	public static Function subStr(String path, int position, int length) {
		return new SubStr(path, position, length);
	}
	
	public static Function toChar(String path, String dateFormat) {
		return new ToChar(path, dateFormat);
	}
	
	public static Function toChar(String path) {
		return new ToChar(path);
	}
	
	public static Function toDate(String path, String dateFormat) {
		return new ToDate(path, dateFormat);
	}

	public static Function toDate(String path) {
		return new ToDate(path);
	}
}

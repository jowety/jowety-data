package com.jowety.data.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ClassConverter implements AttributeConverter<Class, String>{

	@Override
	public String convertToDatabaseColumn(Class attribute) {
		return attribute.getName();
	}

	@Override
	public Class convertToEntityAttribute(String dbData) {
		try {
			return Class.forName(dbData);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}

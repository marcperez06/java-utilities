package io.github.marcperez06.java_utilities.api.rest.objects;

import java.lang.reflect.Type;

import kong.unirest.GenericType;

public class RestGenericObject<T> extends GenericType<T> {
	
	private Type properType;
	
	public RestGenericObject(Type type) {
		this.properType = type;
	}
	
	public Type getType() {
        return this.properType;
    }
}

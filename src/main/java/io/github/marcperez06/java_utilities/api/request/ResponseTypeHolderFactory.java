package io.github.marcperez06.java_utilities.api.request;

public class ResponseTypeHolderFactory {
	
	public static ResponseTypeHolder<String> stringResponse() {
    	return new ResponseTypeHolder<String>() {};
    }
	
	public static ResponseTypeHolder<Void> voidResponse() {
    	return new ResponseTypeHolder<Void>() {};
    }
    
}
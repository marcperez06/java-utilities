package io.github.marcperez06.java_utilities.api.rest.exceptions;

public class RestClientException extends RuntimeException {
	
	public RestClientException(String message) {
		super(message);
	}
	
	public RestClientException(String message, Exception  cause) {
		super(message, cause);
	}
	
	public RestClientException(Exception  cause) {
		this("Rest Client encountred a problem:  " + cause.getMessage(), cause);
	}

}
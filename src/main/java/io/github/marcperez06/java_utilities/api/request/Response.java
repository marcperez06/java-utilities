/**
 * @author Aleix Marques Casanovas
 * modified by @marcperez06
 */
package io.github.marcperez06.java_utilities.api.request;

import java.util.Optional;

public class Response<T> extends ResponseTypeHolder<T> {
	
	public static final int INFORMATIVE = 200;
	public static final int SUCCESS = 200;
	public static final int ACCEPTED = 202;
	public static final int UNAUTHORIZED = 401;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	public static final int SERVER_ERROR = 500;

	private final int statusCode;
	private Optional<String> originalBody;
	private Optional<T> body;
	private Optional<Exception> error;
	
	public Response(int statusCode) {
		this.statusCode = statusCode;
		this.originalBody = Optional.empty();
		this.body = Optional.empty();
		this.error = Optional.empty();
	}
	
	public int getStatusCode() {
		return this.statusCode;
	}
	
	public String getOriginalBody() {
		return this.originalBody.get();
	}
	
	public void setOriginalBody(String jsonResponse) {
		this.originalBody = Optional.ofNullable(jsonResponse);
	}

	public Optional<T> getResponseObject() {
		return body;
	}

	public void setResponseObject(T value) {
		this.body = Optional.ofNullable(value);
	}

	public Optional<Exception> getError() {
		return this.error;
	}

	public void setError(Exception e) {
		this.error = Optional.ofNullable(e);
	}

	/**
	 * Return 'true' if the response status is equals to expected status code, returns 'false' otherwise.
	 * @param expectedStatusCode - int status code expected
	 * @return boolean - true if status is equals to expected, false otherwise
	 */
	public boolean statusCodeIs(int expectedStatusCode) {
		return (this.statusCode == expectedStatusCode);
	}
	
	/**
	 * Return 'true' if the response status is between expected status code, returns 'false' otherwise
	 * Can indicate that status code, need to be included or not in the between expected status code (By default is true)
	 * @param minExpectedStatusCode - Minimum expected status code
	 * @param maxExpectedStatusCode - Maximum expected status code
	 * @param inclusive - If inclusive is true the condition is &gt;= and &lt;=, 
	 * 						and if is false the condition is &gt; and &lt;
	 * @return boolean - true if the status code is between expected values, false otherwise
	 */
	public boolean statusCodeIsBetween(int minExpectedStatusCode, int maxExpectedStatusCode, boolean...inclusive) {
		boolean isBetweenExpectedStatusCode = false;

		if (inclusive != null && inclusive.length > 0 && inclusive[0]) {
			isBetweenExpectedStatusCode = (this.statusCode <= maxExpectedStatusCode && this.statusCode >= minExpectedStatusCode);
		} else {
			isBetweenExpectedStatusCode = (this.statusCode < maxExpectedStatusCode && this.statusCode > minExpectedStatusCode);
		}
		
		return isBetweenExpectedStatusCode;
	}
	
	/**
	 * Return 'true' if the response status is informative (status = 1xx), returns 'false' otherwise.
	 * @return boolean - true if status is informative (status code starts with 1), false otherwise
	 */
	public boolean isInformative() {
		return this.statusCodeIsBetween(99, 200);
	}
	
	/**
	 * Return true if the status code is successful (code: 2XX), false otherwise
	 * @return boolean - true if response is successful, false otherwise
	 */
	public boolean isSuccessful() {
		return this.statusCodeIsBetween(199, 300);
	}
	
	/**
	 * Return 'true' if the response status is success (status = 200), returns 'false' otherwise.
	 * @return boolean - true if status is success, false otherwise
	 */
	public boolean isSuccess() {
		return this.statusCodeIs(SUCCESS);
	}
	
	/**
	 * Return 'true' if the response status is accepted (status = 202), returns 'false' otherwise.
	 * @return boolean - true if status is accepted, false otherwise
	 */
	public boolean isAccepted() {
		return this.statusCodeIs(ACCEPTED);
	}
	
	/**
	 * Return 'true' if the response status is redirection (status = 3xx), returns 'false' otherwise.
	 * @return boolean - true if status is redirection (status code starts with 3), false otherwise
	 */
	public boolean isRedirection() {
		return this.statusCodeIsBetween(299, 400);
	}
	
	/**
	 * Return 'true' if the response status is client error (status = 4xx), returns 'false' otherwise.
	 * @return boolean - true if status is client error (status code starts with 4), false otherwise
	 */
	public boolean isClientError() {
		return this.statusCodeIsBetween(399, 500);
	}

	/**
	 * Return 'true' if the response status is authorized (status != 401), returns 'false' otherwise.
	 * @return boolean - true if status is different to unauthorized, false otherwise
	 */
	public boolean isAuthorized() {
		return !this.statusCodeIs(UNAUTHORIZED);
	}
	
	/**
	 * Return 'true' is the response status is unauthorized (status = 401), returns 'false' otherwise.
	 * @return boolean - true if status is unauthorized, false otherwise
	 */
	public boolean isUnauthorized() {
		return this.statusCodeIs(UNAUTHORIZED);
	}

	/**
	 * Return 'true' is the response status is bad request (status = 400), returns 'false' otherwise.
	 * @return boolean - true if status is bad request, false otherwise
	 */
	public boolean isBadRequest() {
		return this.statusCodeIs(BAD_REQUEST);
	}
	
	/**
	 * Return 'true' is the response status is not found (status = 404), returns 'false' otherwise.
	 * @return boolean - true if status is not found, false otherwise
	 */
	public boolean isNotFound() {
		return this.statusCodeIs(NOT_FOUND);
	}
	
	/**
	 * Return 'true' if the response status is server error (status = 5xx), returns 'false' otherwise.
	 * @return boolean - true if status is server error (status code starts with 5), false otherwise
	 */
	public boolean isServerError() {
		return this.statusCodeIsBetween(499, 600);
	}
	
	/**
	 * Return 'true' is the response status is server error (status = 500), returns 'false' otherwise.
	 * @return boolean - true if status is server error, false otherwise
	 */
	public boolean isInternalServerError() {
		return this.statusCodeIs(SERVER_ERROR);
	}
	
}
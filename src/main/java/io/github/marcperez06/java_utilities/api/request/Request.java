package io.github.marcperez06.java_utilities.api.request;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.github.marcperez06.java_utilities.api.request.credentials.RequestCredentials;
import io.github.marcperez06.java_utilities.api.request.enums.HttpMethodEnum;

public class Request {

	private static final String EMPTY_URL = "";
	private static final String ACCEPT = "Accept";
	private static final String CONTENT_TYPE = "Content-Type";
	public static final String ALL = "*/*";
	public static final String JSON = "application/json";
	public static final String FORM_DATA = "multipart/form-data";
	
	private final HttpMethodEnum method;

	private String url;

	private Map<String, Object> pathParams = new HashMap<String, Object>();
	private Map<String, Object> queryParams = new HashMap<String, Object>();
	private Map<String, String> headers = new HashMap<String, String>();
	private Object body;

	private ResponseTypeHolder<?> responseType;
	
	private RequestCredentials credentials;
	private boolean useCredentials;
	private boolean useBearerToken;
	private boolean sendAsForm;

	public Request(HttpMethodEnum method) {
		this(null, method, EMPTY_URL);
		this.setResponseType(new ResponseTypeHolder<Void>() {});
	}
	
	public Request(HttpMethodEnum method, String url) {
		this(null, method, url);
		this.setResponseType(new ResponseTypeHolder<Void>() {});
	}
	
	public Request(RequestCredentials credentials, HttpMethodEnum method, String url) {
		this.method = method;
		this.sendAsForm = false;
		this.setCredentials(credentials);
		this.setURL(url);
	}

	public void setCredentials(RequestCredentials credentials) {
		this.credentials = credentials;

		if (credentials != null) {
			if (this.credentials.getToken() == null || this.credentials.getToken().isEmpty()) {
				this.useCredentials(true);
			} else {
				this.useBearerToken(true);
			}
		}

		this.useCredentials(credentials != null);
	}

	public void useCredentials(boolean useCredentials) {
		this.useCredentials = (this.credentials != null) ? useCredentials : false;
	}

	public boolean useCredentials() {
		return this.useCredentials;
	}
	
	public void useBearerToken(boolean useBearerToken) {
		this.useBearerToken = (this.credentials != null && !this.credentials.getToken().isEmpty()) ? useBearerToken : false;
	}

	public boolean useBearerToken() {
		return this.useBearerToken;
	}

	public HttpMethodEnum getMethod() {
		return this.method;
	}

	public String getUrl() {
		return this.url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public Map<String, Object> getRouteParams() {
		return this.pathParams;
	}

	public void addPathParam(String key, Object value) {
		this.pathParams.put(key, value);
	}

	public void addPathParams(Map<String, Object> routeParams) {
		this.pathParams.putAll(routeParams);
	}

	public Map<String, Object> getQueryParams() {
		return this.queryParams;
	}

	public void addQueryParam(String key, Object value) {
		this.queryParams.put(key, value);
	}

	public void addQueryParams(Map<String, Object> queryParams) {
		this.queryParams.putAll(queryParams);
	}

	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	public void addHeaders(Map<String, String> headers) {
		this.headers.putAll(headers);
	}

	public Object getBody() {
		return this.body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Type getResponseType() {
		return this.responseType.getType();
	}

	public <T> void setResponseType(ResponseTypeHolder<T> responseType) {
		this.responseType = responseType;
	}

	public RequestCredentials getCredentials() {
		return this.credentials;
	}

	public boolean noResponseObjectNeeded() {
		return this.responseType.getType().equals(Void.TYPE);
	}

	public boolean sendAsForm() {
		return this.sendAsForm;
	}

	public void sendAsForm(boolean sendAsForm) {
		this.sendAsForm = sendAsForm;
	}
	
	public void jsonRequest() {
		this.addHeader(ACCEPT, ALL);
		this.addHeader(CONTENT_TYPE, JSON);
	}
	
	public void formRequest() {
		this.addHeader(ACCEPT, ALL);
		this.addHeader(CONTENT_TYPE, FORM_DATA);
	}
	
}
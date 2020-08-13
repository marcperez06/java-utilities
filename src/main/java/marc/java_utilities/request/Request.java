package marc.java_utilities.request;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.Proxy;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import marc.java_utilities.json.GsonUtils;
import marc.java_utilities.request.credentials.RequestCredentials;

public class Request {

	private static final Response DEFAULT_RESPONSE = new Response();
	
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String PATCH = "PATCH";
	public static final String DELETE = "DELETE";
	
	private static final String UTF_8 = "UTF-8";
	
	private RequestCredentials credentials;
	private boolean useCredentials;
	private boolean useBearerToken;
	private boolean verifySsl;

	private boolean useCertificate;
	private String certificateFilePath;
	private String certificateFilePassword;
	private Map<String, String> extraHeaders;
	
	private boolean useFormParams;
	
	private UnirestInstance uniRest;
	
	public Request() {
		this.credentials = null;
		this.setVerifySsl(true);
		this.useCredentials(false);
	}
	
	public Request(RequestCredentials credentials) {
		this.credentials = credentials;
		this.setVerifySsl(true);
		
		if (this.credentials.getToken() == null || this.credentials.getToken().isEmpty()) {
			this.useCredentials(true);
		} else {
			this.useBearerToken(true);
		}
		
	}

	public void useCredentials(boolean useCredentials) {
		this.useCredentials = (this.credentials != null) ? useCredentials : false;
	}
	
	public void useBearerToken(boolean useBearerToken) {
		this.useBearerToken = (this.credentials != null && !this.credentials.getToken().isEmpty()) ? useBearerToken : false;
	}
	
	public void setVerifySsl(boolean verifySsl) {
		this.verifySsl = verifySsl;
	}
	
	public void useCertificate(boolean useCertificate) {
		this.useCertificate = useCertificate;
	}
	
	public void setCertificateInformation(String certificateFilePath, String certificateFilePassword) {
		this.certificateFilePath = certificateFilePath;
		this.certificateFilePassword = certificateFilePassword;
		this.useCertificate = true;
	}
	
	public void useProxy(RequestProxy proxy) {
		if (this.uniRest != null) {
			Proxy uniRestProxy = new Proxy(proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
			this.uniRest.config().proxy(uniRestProxy);
		}
	}
	
	public void clearProxy() {
		if (this.uniRest != null) {
			this.uniRest.config().proxy(null);
		}
	}
	
	public void resetUnirestConfiguration() {
		if (this.uniRest != null) {
			this.uniRest.config().reset();
			this.uniRest.config().setObjectMapper(new JsonObjectMapper());
		}
	}
	
	private void createUniRest() {
		if (this.uniRest == null) {
			this.uniRest = Unirest.spawnInstance();
			this.uniRest.config().setObjectMapper(new JsonObjectMapper());
			this.useSslVerification();
			this.useCertificate();
		}
	}
	
	private void useSslVerification() {
		if (this.uniRest != null) {
			this.uniRest.config().verifySsl(this.verifySsl);
		}
	}
	
	private void useCertificate() {
		if (this.useCertificate && !this.certificateFilePath.isEmpty()) {
			this.uniRest.config().clientCertificateStore(this.certificateFilePath, this.certificateFilePassword);
		}
	}
	
	/**
	 * Get the extra headers of request
	 * Default headers are the acceptation of Text, Json and Html
	 * @return Map&lt;String, String&gt; - headers
	 */
	public Map<String, String> getExtraHeaders() {
		return this.extraHeaders;
	}
	
	/**
	 * Add an extra header for request
	 * @param key - String
	 * @param value - String
	 */
	public void addExtraHeader(String key, String value) {
		if (this.extraHeaders == null) {
			this.extraHeaders = new HashMap<String, String>();
		}
		this.extraHeaders.put(key, value);
	}
	
	/**
	 * Remove an extra header for request
	 * @param key - String
	 */
	public void removeExtraHeader(String key) {
		if (this.extraHeaders != null) {
			this.extraHeaders.remove(key);
		}
	}
	
	/**
	 * Set the extra headers for request
	 * @param headers - Map&lt;String, String&gt;
	 */
	public void setExtraHeaders(Map<String, String> headers) {
		this.extraHeaders = headers;
	}
	
	public void useFormParams(boolean useFormParams) {
		this.useFormParams = useFormParams;
	}
	
	private void closeUniRest() {
		if (this.uniRest != null) {
			this.uniRest.close();
			this.uniRest = null;
		}
	}
	
	public <Q, B> Response doRequest(String method, String url, Q queryParams, B...bodyParams) {
		return this.doRequest(method, url, queryParams, null, bodyParams);
	}
	
	public <Q, B> Response doRequest(String method, String url, Q queryParams, Map<String, String> routeParams, B...bodyParams) {
		Response response = DEFAULT_RESPONSE;
		boolean canDoRequest = (method != null);
		canDoRequest &= !method.isEmpty();
		
		if (canDoRequest) {
			this.createUniRest();
			
			if (this.uniRest != null) {
				response = this.executeRequest(method, url, queryParams, routeParams, bodyParams);
			}

			this.closeUniRest();

		} else {
			System.out.println("CAN NOT DO ANY REQUEST, METHOD NOT ALLOWED");
		}
		
		return response;
	}
	
	private <Q, B> Response executeRequest(String method, String url, Q queryParams, Map<String, String> routeParams, B...bodyParams) {
		Response response = null;
		String finalUrl = this.getFinalUrl(url, queryParams);

		if (GET.equalsIgnoreCase(method)) {
			response = this.getRequest(finalUrl, routeParams);
		} else if (POST.equalsIgnoreCase(method)) {
			response = this.postRequest(finalUrl, routeParams, bodyParams);
		} else if (PUT.equalsIgnoreCase(method)) {
			response = this.putRequest(finalUrl, routeParams, bodyParams);
		} else if (PATCH.equalsIgnoreCase(method)) {
			response = this.patchRequest(finalUrl, routeParams, bodyParams);
		} else if (DELETE.equalsIgnoreCase(method)) {
			response = this.deleteRequest(finalUrl, routeParams, bodyParams);
		}
		
		return response;
	}
	
	private <Q> String getFinalUrl(String url, Q queryParams) {
		return url + this.createQueryParams(queryParams);
	}
	
	private <T> String createQueryParams(T queryParams) {
		String uriParams = "";
		
		if (queryParams != null) {
			
			if (queryParams instanceof String) {
				String params = (String) queryParams;
				
				if (!params.isEmpty()) {
					
					if (params.startsWith("?")) {
						uriParams = params;
					} else {
						uriParams = "?" + params;
					}
					
				}

			} else {
				uriParams = "?" + this.getUriParams(queryParams);
			}

		}
		
		return uriParams;
	}
	
	private <T> String getUriParams(T queryParams) {
		String uriParams = "";
		
		if (queryParams instanceof String) {
			uriParams = queryParams.toString();
		} else {
			uriParams = this.getUriParamsFromObject(queryParams);
		}
		
		return uriParams;
	}
	
	@SuppressWarnings("unchecked")
	private <T> String getUriParamsFromObject(T queryParams) {
		String uriParams = "";
		StringBuilder builder = new StringBuilder();
		String concatParams = "&";
		
		try {
			
			Class<T> clazz = (Class<T>) queryParams.getClass();
			Field[] fields = clazz.getDeclaredFields();
			
			for (Field field : fields) {
				field.setAccessible(true);
				String fieldName = field.getName();
				Object value = field.get(queryParams);
				
				if (value != null) {
					builder.append(URLEncoder.encode(fieldName, UTF_8));
					builder.append("=");
					builder.append(URLEncoder.encode(value.toString(), UTF_8));
					builder.append(concatParams);
				}
			}
			
			uriParams = builder.toString();
			uriParams = uriParams.substring(0, uriParams.length() - concatParams.length());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return uriParams;
	}
	
	private Response getRequest(String url, Map<String, String> routeParams) {
		Response response = DEFAULT_RESPONSE;
		
		GetRequest get = this.uniRest.get(url);
		
		if (get != null) {
			this.addBaseHeaders(get);
			this.addCredentials(get);
			this.replaceRouteParams(get, routeParams);

			HttpResponse<String> responseOfRequest = get.asString();
			
			response = this.createResponse(responseOfRequest);
		}
		
		return response;
	}

	private <T> Response postRequest(String url, Map<String, String> routeParams, T...bodyParams) {
		Response response = DEFAULT_RESPONSE;
		HttpRequestWithBody post = this.uniRest.post(url);
		response = this.getResponse(post, routeParams, bodyParams);
		return response;
	}
	
	private <T> Response putRequest(String url, Map<String, String> routeParams, T...bodyParams) {
		Response response = DEFAULT_RESPONSE;
		HttpRequestWithBody put = this.uniRest.put(url);
		response = this.getResponse(put, routeParams, bodyParams);
		return response;
	}
	
	private <T> Response patchRequest(String url, Map<String, String> routeParams, T...bodyParams) {
		Response response = DEFAULT_RESPONSE;
		HttpRequestWithBody patch = this.uniRest.patch(url);
		response = this.getResponse(patch, routeParams, bodyParams);
		return response;
	}
	
	private <T> Response deleteRequest(String url, Map<String, String> routeParams, T...bodyParams) {
		Response response = null;
		HttpRequestWithBody delete = this.uniRest.delete(url);
		response = this.getResponse(delete, routeParams, bodyParams);
		return response;
	}

	private void addBaseHeaders(HttpRequest<?> request) {
		if (request != null && this.extraHeaders != null) {
			request.headers(this.extraHeaders);
		} else {
			String accepts = "application/json, text/html, text/plain";
			request.accept(accepts);
			request.header("Content-Type", "application/json");
		}
		
		request.header("Connection", "close");
	}
	
	private void addCredentials(HttpRequest<?> request) {
		boolean addCredentials = this.useCredentials || this.useBearerToken;
		addCredentials &= (this.credentials != null);
		
		if (addCredentials) {
			
			if (useBearerToken) {
				request.header("Authorization", "Bearer " + this.credentials.getToken());
			} else {
				String user = this.credentials.getUser();
				String password = this.credentials.getPassword();
				
				request.basicAuth(user, password);
			}

		}
	}

	private <T> Response getResponse(HttpRequestWithBody request, Map<String, String> routeParams, T...bodyParams) {
		Response response = null;
		
		if (request != null) {
			this.addBaseHeaders(request);
			this.addCredentials(request);
			this.replaceRouteParams(request, routeParams);
			response = this.sendRequest(request, bodyParams);
		}
		
		return response;
	}

	private void replaceRouteParams(HttpRequest<?> request, Map<String, String> routeParams) {
		if (routeParams != null) {
			for (Entry<String, String> param : routeParams.entrySet()) {
				request.routeParam(param.getKey(), param.getValue());
			}
		}
	}
	
	private <T> Response sendRequest(HttpRequestWithBody request, T...bodyParams) {
		Response response = null;
		HttpResponse<String> responseOfRequest = null;
		
		if (bodyParams != null && bodyParams.length > 0) {

			if (useFormParams) {
				MultipartBody requestWithParam = this.addFormParams(request, bodyParams);
				responseOfRequest = requestWithParam.asString();
			} else {
				RequestBodyEntity requestWithParam = this.addBodyParams(request, bodyParams);
				responseOfRequest = requestWithParam.asString();
			}
			
		} else {
			responseOfRequest = request.asString();
		}
		
		response = this.createResponse(responseOfRequest);
		return response;
	}
	
	private <T> MultipartBody addFormParams(HttpRequestWithBody request, T...bodyParams) {
		MultipartBody requestWithParam = null;
		if (bodyParams != null && bodyParams.length > 0) {
			
			T bodyParam = bodyParams[0];
			Field[] fields = bodyParam.getClass().getDeclaredFields();
			
			for (Field field : fields) {
				field.setAccessible(true);
				String fieldName = field.getName();

				try {
					request.field(fieldName, field.get(bodyParam));
				} catch (Exception e) {
					// Omit the exception
				}
				
			}

		}
		return requestWithParam;
	}
	
	private <T> RequestBodyEntity addBodyParams(HttpRequestWithBody request, T...bodyParams) {
		RequestBodyEntity requestWithParam = null;
		if (bodyParams != null && bodyParams.length > 0) {
			requestWithParam = this.addBodyParam(request, bodyParams[0]);
		}
		return requestWithParam;
	}

	private <T> RequestBodyEntity addBodyParam(HttpRequestWithBody request, T bodyParam) {
		RequestBodyEntity requestWithParams = null;
		
		if (bodyParam != null) {
			requestWithParams = request.body(GsonUtils.getJSON(bodyParam));
		}
		
		return requestWithParams;
	}
	
	private Response createResponse(HttpResponse<?> responseOfRequest) {
		Response response = DEFAULT_RESPONSE;
		int statusCode = -1;
		try {
			String bodyResponse = responseOfRequest.getBody().toString();
			statusCode = responseOfRequest.getStatus();
			response = new Response(statusCode, bodyResponse);
		} catch (Exception e) {
			System.out.println("--- Error creating response");
			e.printStackTrace();
		}
		return response;
	}

}
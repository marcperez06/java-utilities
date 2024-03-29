package io.github.marcperez06.java_utilities.api.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.RequestProxy;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.rest.exceptions.RestClientException;
import io.github.marcperez06.java_utilities.api.rest.objects.RestGenericObject;
import kong.unirest.ContentType;
import kong.unirest.Header;
import kong.unirest.Headers;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Proxy;
import kong.unirest.Unirest;
import kong.unirest.gson.GsonObjectMapper;

public class UnirestClient extends BaseRestClient {

    protected Optional<Proxy> proxy;
    private ObjectMapper objectMapper;

    public UnirestClient() {
    	super();
    	this.proxy = Optional.empty();
    	this.objectMapper = new GsonObjectMapper();
        Unirest.config().setObjectMapper(objectMapper);
    }

    @Override
    public void verifySsl(boolean verifySsl) {
        Unirest.config().verifySsl(verifySsl);
    }

    @Override
    public void setProxy(RequestProxy requestProxy) {
        this.proxy = Optional.of(new Proxy(requestProxy.getHost(), requestProxy.getPort(), 
        									requestProxy.getUsername(), requestProxy.getPassword()));
        this.useProxy();
    }

    @Override
    public void useCertificate() {
        if (super.certificateFilePath.isPresent() && super.certificateFilePassword.isPresent()) {
            Unirest.config().clientCertificateStore(super.certificateFilePath.get(), super.certificateFilePassword.get());
            super.useCertificate = true;
        } else {
            throw new RestClientException("To use certificate a Certificate File Path and Certificate File Password must be provided.");
        }
    }

    @Override
    public void disableCertificate() {
        super.useCertificate = false;
        this.resetConfiguration();
    }

    @Override
    public void useProxy() {
        if (this.proxy.isPresent()) {
            Unirest.config().proxy(this.proxy.get());
            super.useProxy = true;
        } else {
            throw new RestClientException("To use a Proxy, a Proxy configuration must be provided.");
        }
    }

    @Override
    public void disableProxy() {
        super.useProxy = false;
        this.resetConfiguration();
    }
    
    @Override
    public void cookieManagement(boolean cookieManagement) {
    	super.cookieManagement = cookieManagement;
    	Unirest.config().enableCookieManagement(cookieManagement);
    }
    
    public void disableCookieManagement() {
    	this.cookieManagement(false);
    }
    
    public void enableCookieManagement() {
    	this.cookieManagement(true);
    }

    public void setObjectMapper(ObjectMapper mapper) {
        this.objectMapper = mapper;
        Unirest.config().setObjectMapper(mapper);
    }

    public void resetObjectMapper() {
        this.objectMapper = new GsonObjectMapper();
        Unirest.config().setObjectMapper(this.objectMapper);
    }

    private void resetConfiguration() {
        Unirest.shutDown();
        Unirest.config().setObjectMapper(this.objectMapper);
        
        if (super.useCertificate) {
            this.useCertificate();
        }
        
        if (super.useProxy) {
            this.useProxy();
        }
        
        this.cookieManagement(super.cookieManagement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Response<T> send(Request request) {
    	HttpResponse<T> unirestResponse = null;
    	
        try {
        	HttpRequest<?> unirestRequest = this.createRequest(request);
        	
        	this.addCredentials(unirestRequest, request);
        	this.addRequestParams(unirestRequest, request);
			
			if (request.noResponseObjectNeeded()) {
	            unirestResponse = (HttpResponse<T>) unirestRequest.asEmpty();
	        } else {
	            unirestResponse = unirestRequest.asObject(new RestGenericObject<T>(request.getResponseType()));
	        }
        	
        } catch (RestClientException e) {
        	throw e;
        }

        return this.asResponse(unirestResponse);
    }
    
    private HttpRequest<?> createRequest(Request request) throws RestClientException {
    	HttpRequest<?> unirestRequest = null;
    	switch (request.getMethod()) {
	        case GET:
	            unirestRequest = Unirest.get(request.getUrl());
	            break;
	        case POST:
	            unirestRequest = Unirest.post(request.getUrl());
	            break;
	        case PUT:
	            unirestRequest = Unirest.put(request.getUrl());
	            break;
	        case PATCH:
	            unirestRequest = Unirest.patch(request.getUrl());
	            break;
	        case DELETE:
	            unirestRequest = Unirest.delete(request.getUrl());
	            break;
	        default:
	            throw new RestClientException("Unable to determine HttpMethod for Request");
	    }
    	return unirestRequest;
    }
    
    private void addCredentials(HttpRequest<?> unirestRequest, Request request) {
    	if (request.useCredentials()) {
			if (request.useBearerToken()) {
				String token = request.getCredentials().getToken();
				unirestRequest = unirestRequest.header("Authorization", "Bearer " + token);
			} else {
				String user = request.getCredentials().getUser();
				String password = request.getCredentials().getPassword();
				unirestRequest = unirestRequest.basicAuth(user, password);
			}
		}
    }
    
    private void addRequestParams(HttpRequest<?> unirestRequest, Request request) {
		unirestRequest.routeParam(request.getRouteParams());
		unirestRequest.queryString(request.getQueryParams());
		unirestRequest.headers(request.getHeaders());

		if (unirestRequest instanceof HttpRequestWithBody) {
			if (request.sendAsForm()) {
				this.sendBodyAsFormData(request, unirestRequest);
			} else {
				unirestRequest = ((HttpRequestWithBody) unirestRequest).body(request.getBody());
			}
		}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void sendBodyAsFormData(Request request, HttpRequest unirestRequest) {
    	HttpRequestWithBody body = (HttpRequestWithBody) unirestRequest;
        Map<String, Object> map = (Map<String, Object>) request.getBody();
        for (Entry<String, Object> entry : map.entrySet()) {
        	String key = entry.getKey();
        	Object value = entry.getValue();
        	
        	if (value instanceof File) {
        		
				try {
					File file = (File) value;
            		InputStream inputStream = new FileInputStream(file);
            		body.field(key, inputStream, ContentType.MULTIPART_FORM_DATA, file.getName());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
        		
        	} else {
        		body.field(key, value);
        	}
        }
    }
    
    private <T> Response<T> asResponse(HttpResponse<T> unirestResponse) {
        Response<T> response = new Response<>(unirestResponse.getStatus());
        response.setHeaders(this.getResponseHeaders(unirestResponse));
        response.setResponseObject(unirestResponse.getBody());
        if (unirestResponse.getParsingError().isPresent()) {
            response.setOriginalBody(unirestResponse.getParsingError().get().getOriginalBody());
            response.setError(unirestResponse.getParsingError().get());
        }

        return response;
    }
    
    private <T> Map<String, String> getResponseHeaders(HttpResponse<T> unirestResponse) {
    	List<Header> unirestHeaders = unirestResponse.getHeaders().all();
    	Map<String, String> responseHeaders = unirestHeaders.stream()
    											.collect(Collectors.toMap(Header::getName, Header::getValue));
    	return responseHeaders;
    }

}
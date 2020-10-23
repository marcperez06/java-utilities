package io.github.marcperez06.java_utilities.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kong.unirest.ContentType;
import kong.unirest.GenericType;
import kong.unirest.GsonObjectMapper;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.ObjectMapper;
import kong.unirest.Proxy;
import kong.unirest.Unirest;
import sogeti.jira_api_wrapper.core.exceptions.JiraApiWrapperException;
import sogeti.jira_api_wrapper.core.utils.request.Request;
import sogeti.jira_api_wrapper.core.utils.request.RequestProxy;
import sogeti.jira_api_wrapper.core.utils.request.Response;

public class UnirestClient implements RestClient {
    private static final Log log = LogFactory.getLog(UnirestClient.class);

    private Optional<String> certificateFilePath = Optional.empty();
    private Optional<String> certificateFilePassword = Optional.empty();

    private boolean useCertificate = false;
    private boolean useProxy = false;

    private Optional<Proxy> proxy = Optional.empty();

    private ObjectMapper objectMapper = new GsonObjectMapper();

    public UnirestClient() {
        Unirest.config().setObjectMapper(objectMapper);
    }

    @Override
    public void verifySsl(boolean verifySsl) {
        Unirest.config().verifySsl(verifySsl);
    }

    @Override
    public void setCertificate(String certificateFilePath, String certificateFilePassword) {
        this.certificateFilePath = Optional.of(certificateFilePath);
        this.certificateFilePassword = Optional.of(certificateFilePassword);
        this.useCertificate();
    }

    @Override
    public void setProxy(RequestProxy requestProxy) {
        this.proxy = Optional.of(new Proxy(requestProxy.getHost(), requestProxy.getPort(), requestProxy.getUsername(), requestProxy.getPassword()));
        useProxy();
    }

    @Override
    public void useCertificate() {
        if (certificateFilePath.isPresent() && certificateFilePassword.isPresent()) {
            Unirest.config().clientCertificateStore(certificateFilePath.get(), certificateFilePassword.get());
            useCertificate = true;
        } else {
            throw new JiraApiWrapperException("To use certificate a Certificate File Path and Certificate File Password must be provided.");
        }
    }

    @Override
    public void disableCertificate() {
        useCertificate = false;
        resetConfiguration();
    }

    @Override
    public void useProxy() {
        if (proxy.isPresent()) {
            Unirest.config().proxy(proxy.get());
            useProxy = true;
        } else {
            throw new JiraApiWrapperException("To use a Proxy, a Proxy configuration must be provided.");
        }
    }

    @Override
    public void disableProxy() {
        useProxy = false;
        resetConfiguration();
    }

    public void setObjectMapper(ObjectMapper mapper) {
        this.objectMapper = mapper;
        Unirest.config().setObjectMapper(mapper);
    }

    public void resetObjectMapper() {
        objectMapper = new GsonObjectMapper();
        Unirest.config().setObjectMapper(objectMapper);
    }

    private void resetConfiguration() {
        Unirest.shutDown();
        Unirest.config().setObjectMapper(objectMapper);
        if (useCertificate) {
            useCertificate();
        }
        if (useProxy) {
            useProxy();
        }
    }

    @Override
    @SuppressWarnings("uncheked")
    public <T> Response<T> send(Request request) {
        @SuppressWarnings("rawtypes")
        HttpRequest unirestRequest;
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
                log.debug(request);
                throw new JiraApiWrapperException("Unable to determine HttpMethod for Request");
        }

        unirestRequest = unirestRequest.routeParam(request.getRouteParams())
            .queryString(request.getQueryParams())
            .headers(request.getHeaders());

        if (unirestRequest instanceof HttpRequestWithBody) {
            if (request.sendAsForm()) {
                this.sendBodyAsFormData(request, unirestRequest);
            } else {
                unirestRequest = ((HttpRequestWithBody) unirestRequest).body(request.getBody());
            }
        }

        if (request.useCredentials()) {
            if (request.useBearerToken()) {
                unirestRequest = unirestRequest.header("Authorization", "Bearer " + request.getCredentials().getToken());
            } else {
                unirestRequest = unirestRequest.basicAuth(request.getCredentials().getUser(), request.getCredentials().getPassword());
            }
        }
        
        HttpResponse<T> unirestResponse;

        if (request.noResponseObjectNeeded()) {
            unirestResponse = unirestRequest.asEmpty();
        } else {
            unirestResponse = unirestRequest.asObject(new GenericObject<T>(request.getResponseType()));
        }

        return asResponse(unirestResponse);
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
        response.setResponseObject(unirestResponse.getBody());
        if (unirestResponse.getParsingError().isPresent()) {
            response.setOriginalBody(unirestResponse.getParsingError().get().getOriginalBody());
            response.setError(unirestResponse.getParsingError().get());
        }

        return response;
    }
    
    class GenericObject<T> extends GenericType<T> {
    	private Type properType;
    	
    	public GenericObject(Type type) {
    		this.properType = type;
    	}
    	
    	public Type getType() {
            return this.properType;
        }
    }

}
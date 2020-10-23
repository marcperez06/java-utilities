/**
 * @author Aleix Marques Casanovas
 * modified by @marcperez06
 */
package io.github.marcperez06.java_utilities.api.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.RequestProxy;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.rest.exceptions.RestClientException;
import io.github.marcperez06.java_utilities.api.rest.interfaces.RestClient;
import io.github.marcperez06.java_utilities.api.rest.objects.RestGenericObject;
import kong.unirest.ContentType;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Proxy;
import kong.unirest.Unirest;
import kong.unirest.gson.GsonObjectMapper;

public class UnirestClient implements RestClient {
    private static final Log log = LogFactory.getLog(UnirestClient.class);

    private boolean useCertificate;
    private boolean useProxy;
    private Optional<String> certificateFilePath;
    private Optional<String> certificateFilePassword;
    private Optional<Proxy> proxy;
    private ObjectMapper objectMapper;

    public UnirestClient() {
    	this.useCertificate = false;
    	this.useProxy = false;
    	this.certificateFilePath = Optional.empty();
    	this.certificateFilePassword = Optional.empty();
    	this.proxy = Optional.empty();
    	this.objectMapper = new GsonObjectMapper();
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
            throw new RestClientException("To use certificate a Certificate File Path and Certificate File Password must be provided.");
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
            throw new RestClientException("To use a Proxy, a Proxy configuration must be provided.");
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
    @SuppressWarnings("unchecked")
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
                throw new RestClientException("Unable to determine HttpMethod for Request");
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
            unirestResponse = unirestRequest.asObject(new RestGenericObject<T>(request.getResponseType()));
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

}
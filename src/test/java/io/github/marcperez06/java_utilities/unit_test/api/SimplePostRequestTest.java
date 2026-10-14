package io.github.marcperez06.java_utilities.unit_test.api;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.request.ResponseTypeHolder;
import io.github.marcperez06.java_utilities.api.request.enums.HttpMethodEnum;
import io.github.marcperez06.java_utilities.api.rest.UnirestClient;
import io.github.marcperez06.java_utilities.api.rest.interfaces.IRestClient;
import io.github.marcperez06.java_utilities.json.GsonUtils;
import io.github.marcperez06.java_utilities.unit_test.api.domain.BodyPostParamsExample;
import io.github.marcperez06.java_utilities.unit_test.api.domain.ResponsePostExample;

public class SimplePostRequestTest {
	
	private IRestClient api;
	
	@Before
	public void beforeTest() {
		System.out.println("----------- Simple POST Request using UnirestClient -----------------");
		this.api = new UnirestClient(); 
	}
	
	@Test
	public void simplePostRequestTest() {
		String url = "https://jsonplaceholder.typicode.com/posts";
		Request request = new Request(HttpMethodEnum.POST, url);
		request.setResponseType(new ResponseTypeHolder<ResponsePostExample>() {});
		request.jsonRequest();
		request.setBody(new BodyPostParamsExample("test", "body Test", 78912));
		Response<ResponsePostExample> response = api.send(request);
		this.printResponse(response);
		assert response.isSuccessful();
	}
	
	@Test
	public void useGenericMapAsBody() {
		String url = "https://jsonplaceholder.typicode.com/posts";
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("title", "test");
		body.put("body", "body test");
		body.put("userId", Integer.valueOf(78912));
		
		Request request = new Request(HttpMethodEnum.POST, url);
		request.setResponseType(new ResponseTypeHolder<HashMap<String, Object>>() {});
		request.jsonRequest();
		request.setBody(body);
		Response<HashMap<String, Object>> response = api.send(request);
		this.printResponse(response);
		assert response.isSuccessful();
	}
	
	@Test
	public void sendJsonStringAsBody() {
		String url = "https://jsonplaceholder.typicode.com/posts";
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("title", "test");
		body.put("body", "body test");
		body.put("userId", Integer.valueOf(78912));
		
		Request request = new Request(HttpMethodEnum.POST, url);
		request.setResponseType(new ResponseTypeHolder<HashMap<String, Object>>() {});
		request.jsonRequest();
		request.setBody(GsonUtils.getPrettyJSON(body));
		Response<HashMap<String, Object>> response = api.send(request);
		this.printResponse(response);
		assert response.isSuccessful();
	}
	
	private <T> void printResponse(Response<T> response) {
		if (response.isSuccessful()) {
			String body = response.getResponseObject().get().toString();
			System.out.println(body);
		} else {
			System.out.println("Response is not successful, Status: " + response.getStatusCode());
		}
	}

}

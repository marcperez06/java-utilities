package io.github.marcperez06.java_utilities.unit_test.api;

import org.junit.Before;
import org.junit.Test;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.request.ResponseTypeHolder;
import io.github.marcperez06.java_utilities.api.request.enums.HttpMethodEnum;
import io.github.marcperez06.java_utilities.api.rest.UnirestClient;
import io.github.marcperez06.java_utilities.api.rest.interfaces.IRestClient;
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
		String url = "https://reqbin.com/sample/post/json";
		Request request = new Request(HttpMethodEnum.POST, url);
		request.setResponseType(new ResponseTypeHolder<ResponsePostExample>() {});
		request.jsonRequest();
		request.setBody(new BodyPostParamsExample(78912));
		Response<ResponsePostExample> response = api.send(request);
		this.printResponse(response);
		assert response.isSuccessful();
	}
	
	private void printResponse(Response<ResponsePostExample> response) {
		if (response.isSuccess()) {
			String body = response.getResponseObject().get().toString();
			System.out.println(body);
		} else {
			System.out.println("Response is not successful, Status: " + response.getStatusCode());
		}
	}

}

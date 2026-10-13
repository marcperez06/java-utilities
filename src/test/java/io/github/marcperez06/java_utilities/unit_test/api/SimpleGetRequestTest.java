package io.github.marcperez06.java_utilities.unit_test.api;

import org.junit.Before;
import org.junit.Test;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.request.ResponseTypeHolder;
import io.github.marcperez06.java_utilities.api.request.ResponseTypeHolderFactory;
import io.github.marcperez06.java_utilities.api.request.enums.HttpMethodEnum;
import io.github.marcperez06.java_utilities.api.rest.UnirestClient;
import io.github.marcperez06.java_utilities.api.rest.interfaces.IRestClient;

public class SimpleGetRequestTest {
	
	private IRestClient api;
	
	@Before
	public void beforeTest() {
		System.out.println("----------- Simple GET Request using UnirestClient -----------------");
		this.api = new UnirestClient(); 
	}
	
	@Test
	public void simpleGetRequestTest() {
		String url = "https://datos.gob.es/apidata/catalog/dataset";
		Request request = new Request(HttpMethodEnum.GET, url);
		request.setResponseType(ResponseTypeHolderFactory.stringResponse());
		this.printResponse(api.send(request));
	}
	
	private void printResponse(Response<String> response) {
		if (response.isSuccess()) {
			String body = response.getOriginalBody();
			System.out.println(body);
		} else {
			System.out.println("Response is not successful, Status: " + response.getStatusCode());
		}
	}

}

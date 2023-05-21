package io.github.marcperez06.java_utilities.unit_test.api;

import org.junit.Before;
import org.junit.Test;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.request.ResponseTypeHolder;
import io.github.marcperez06.java_utilities.api.request.enums.HttpMethodEnum;
import io.github.marcperez06.java_utilities.api.rest.UnirestClient;
import io.github.marcperez06.java_utilities.api.rest.interfaces.IRestClient;

public class GetRequestWithPathParamsTest {
	
	private IRestClient api;
	
	@Before
	public void beforeTest() {
		System.out.println("----------- GET Request with path params using UnirestClient -----------------");
		this.api = new UnirestClient(); 
	}
	
	@Test
	public void getRequestWithPathParamsTest() {
		String pathParam = "l01281230-calidad-del-aire";
		Request request = new Request(HttpMethodEnum.GET, "https://datos.gob.es/apidata/catalog/dataset/{id}");
		request.setResponseType(new ResponseTypeHolder<String>() {});
		request.addPathParam("id", pathParam);
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

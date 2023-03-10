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

public class GetRequestWithQueryParmsTest {
	
	private UnirestClient api;
	
	@Before
	public void beforeTest() {
		System.out.println("----------- GET Request with Query Params using UnirestClient -----------------");
		this.api = new UnirestClient(); 
	}
	
	@Test
	public void simpleGetRequestTest() {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("_pageSize", Integer.valueOf(5));
		queryParams.put("_page", Integer.valueOf(1));
		
		Request request = new Request(HttpMethodEnum.GET, "https://datos.gob.es/apidata/catalog/dataset");
		request.setResponseType(new ResponseTypeHolder<String>() {});
		// Add only one query param
		request.addQueryParam("_sort", "title");
		// Add multiple query params using a map
		request.addQueryParams(queryParams);
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

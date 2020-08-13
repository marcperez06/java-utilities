package marc.java_utilities.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import marc.java_utilities.json.GsonUtils;
import marc.java_utilities.reflection.ReflectionUtils;

public class Response {

	private int statusCode;
	private String bodyResponse;
	
	public Response() {
		this.statusCode = -1;
		this.bodyResponse = "";
	}
	
	public Response(int statusCode, String jsonResponse) {
		this.statusCode = statusCode;
		this.bodyResponse = jsonResponse;
	}
	
	public int getStatusCode() {
		return this.statusCode;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getBodyResponse() {
		return this.bodyResponse;
	}
	
	public void setBodyResponse(String jsonResponse) {
		this.bodyResponse = jsonResponse;
	}
	
	/**
	 * Return true if the status code is successful (code: 2XX), false otherwise
	 * @return boolean - true if response is successful, false otherwise
	 */
	public boolean isSuccessful() {
		return this.statusCode < 300 && this.statusCode >= 200;
	}
	
	/**
	 * Return a object of class specified.
	 * @param <T> - Generic Object
	 * @param clazz - Class of object you wish return
	 * @return T - Object of class specified.
	 */
	public <T> T getResponseObject(Class<T> clazz) {
		T responseObject = (T) GsonUtils.returnJsonbject(this.bodyResponse, clazz);
		
		if (responseObject == null) {
			List<T> responseObjectList = this.getResponseObjectList(clazz);
			if (!responseObjectList.isEmpty()) {
				responseObject = responseObjectList.get(0);
			}
		}
		
		return responseObject;
	}
	
	/**
	 * Return a object list of class specified.
	 * @param <T> - Generic Object of each element list
	 * @param clazz - Class of object inside list you wish return
	 * @return T - Object List of class specified.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> getResponseObjectList(Class<T> clazz) {
		List<T> responseObjectList = new ArrayList<T>();
		if (!this.bodyResponse.isEmpty()) {
			T responseObject = (T) GsonUtils.returnJsonbject(this.bodyResponse, List.class);
			if (responseObject instanceof List) {
				List<T> list = (List<T>) responseObject;
				for (T object : list) {
					if (object instanceof Map) {
						Map map = (Map) object;
						T obj = (T) ReflectionUtils.getSimpleObjectByMap(map, clazz);
						responseObjectList.add(obj);
					} else {
						responseObjectList.add(object);
					}
				}
				
			} else {
				responseObjectList.add(responseObject);
			}
		}
		return responseObjectList;
	}

}
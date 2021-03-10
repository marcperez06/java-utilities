package io.github.marcperez06.java_utilities.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;

public class GsonUtils {
	public static final String ESCAPING_HTML = "escaping html";
	public static final String NOT_ESCAPING_HTML = "Not escaping html";
	
	private GsonUtils() {}
	
	private static Gson createGson(String type, String dateFormat) {
		Gson gson = null;
		
		if (type.equals(ESCAPING_HTML)) {
			if (dateFormat != null) {
				gson = new GsonBuilder().setDateFormat(dateFormat).create();
			} else {
				gson = new Gson();
			}
		} else if (type.equals(NOT_ESCAPING_HTML)) {
			if (dateFormat != null) {
				gson = new GsonBuilder().setDateFormat(dateFormat).disableHtmlEscaping().create();
			} else {
				gson = new GsonBuilder().disableHtmlEscaping().create();
			}
		} else {
			gson = new Gson();
		}

		return gson;
	}
	
	/**
	 * Return object based in JSON, escaping the HTML characters 
	 * and transform the date values in format specified
	 * @param <T> - Generic type of object
	 * @param json - String in JSON format
	 * @param type - Class of type that be returned
	 * @return Object of type specified with all properties filled if exist in json - T
	 */
	public static <T> T returnJsonbject(String json, Class<T> type) {
		return returnJsonObject(json, type, null);
	}
	
	/**
	 * Return object based in JSON, escaping the HTML characters 
	 * and transform the date values in format specified
	 * @param <T> - Generic type of object
	 * @param json - String in JSON format
	 * @param type - Class of type that be returned
	 * @param dateFormat - String date format
	 * @return Object of type specified with all properties filled if exist in json - T
	 */
	public static <T> T returnJsonObject(String json, Class<T> type, String dateFormat) {
		T jsonObject = null;
		try {
			Gson gson = createGson(ESCAPING_HTML, dateFormat);
			jsonObject = gson.fromJson(json, type);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = null;
		}
		return jsonObject;
	}
	
	/**
	 * Return a object list of class specified, escaping the HTML characters 
	 * @param <T> - Generic Object of each element list
	 * @param json - String
	 * @param type - Class of object inside list you wish return
	 * @return Object List of class specified - List&lt;T&gt;
	 */
	public static <T> List<T> returnJsonObjectList(String json, Class<T> type) {
		return returnJsonObjectList(json, type, null);
	}
	
	/**
	 * Return a object list of class specified, escaping the HTML characters 
	 * and transform the date values in format specified
	 * @param <T> - Generic Object of each element list
	 * @param json - String
	 * @param type - Class of object inside list you wish return
	 * @param dateFormat - String format of the date (Example: 'dd/MM/yyy HH:mm:ss')
	 * @return Object List of class specified - List&lt;T&gt;
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> returnJsonObjectList(String json, Class<T> type, String dateFormat) {
		List<T> jsonObjectList = new ArrayList<T>();
		
		try {
			if (json != null && !json.isEmpty()) {
				T jsonObject = (T) returnJsonObject(json, List.class, dateFormat);
				if (jsonObject instanceof List) {
					List<T> list = (List<T>) jsonObject;
					for (T object : list) {
						if (object instanceof Map) {
							Map map = (Map) object;
							T obj = (T) ReflectionUtils.getSimpleObjectByMap(map, type);
							jsonObjectList.add(obj);
						} else {
							jsonObjectList.add(object);
						}
					}
					
				} else {
					jsonObjectList.add(jsonObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonObjectList;
	}

	/**
	 * Return object based in JSON, without escaping the HTML characters 
	 * and transform the date values in format specified
	 * @param <T> - Generic type of object
	 * @param json - String in JSON format
	 * @param type - Class of type that be returned
	 * @return Object of type specified with all properties filled if exist in json - T
	 */
	public static <T> T returnJsonObjectWithoutEscapingHtml(String json, Class<T> type) {
		return returnJsonObjectWithoutEscapingHtml(json, type, null);
	}
	
	/**
	 * Return object based in JSON, without escaping the HTML characters 
	 * and transform the date values in format specified
	 * @param <T> - Generic type of object
	 * @param json - String in JSON format
	 * @param type - Class of type that be returned
	 * @param dateFormat - String date format
	 * @return Object of type specified with all properties filled if exist in json - T
	 */
	public static <T> T returnJsonObjectWithoutEscapingHtml(String json, Class<T> type, String dateFormat) {
		T jsonObject = null;
		try {
			Gson gson = createGson(NOT_ESCAPING_HTML, dateFormat);
			jsonObject = gson.fromJson(json, type);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = null;
		}
		return jsonObject;
	}
	
	/**
	 * Return a object list of class specified, without escaping the HTML characters
	 * @param <T> - Generic Object of each element list
	 * @param json - String of json
	 * @param type - Class of object inside list you wish return
	 * @return Object List of class specified - List&lt;T&gt;
	 */
	public static <T> List<T> returnJsonObjectListWithoutEscapingHtml(String json, Class<T> type) {
		return returnJsonObjectList(json, type, null);
	}
	
	/**
	 * Return a object list of class specified, without escaping the HTML characters 
	 * and transform the date values in format specified
	 * @param <T> - Generic Object of each element list
	 * @param json - String of json
	 * @param type - Class of object inside list you wish return
	 * @param dateFormat - String format of the date (Example: 'dd/MM/yyy HH:mm:ss')
	 * @return Object List of class specified - List&lt;T&gt;
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> returnJsonObjectListWithoutEscapingHtml(String json, Class<T> type, 
																								String dateFormat) {
		
		List<T> jsonObjectList = new ArrayList<T>();
		
		try {
			if (json != null && !json.isEmpty()) {
				T jsonObject = (T) returnJsonObjectWithoutEscapingHtml(json, List.class, dateFormat);
				if (jsonObject instanceof List) {
					List<T> list = (List<T>) jsonObject;
					for (T object : list) {
						if (object instanceof Map) {
							Map map = (Map) object;
							T obj = (T) ReflectionUtils.getSimpleObjectByMap(map, type);
							jsonObjectList.add(obj);
						} else {
							jsonObjectList.add(object);
						}
					}
					
				} else {
					jsonObjectList.add(jsonObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonObjectList;
	}
	
	/**
	 * Return the object in JSON format
	 * @param <T> - Generic type of object
	 * @param obj - Object of generic type
	 * @return The object in JSON format with all properties - String (Example: { name : "example", id : 1 })
	 */
	public static <T> String getJSON(T obj) {
		return getJSON(obj, null);
	}
	
	/**
	 * Return the object in JSON format, and transform the date values in format specified 
	 * @param <T> - Generic type of object
	 * @param obj - Object of generic type
	 * @param dateFormat - String date format
	 * @return The object in JSON format with all properties - String (Example: { name : "example", id : 1 }) 
	 */
	public static <T> String getJSON(T obj, String dateFormat) {
		String json = "";
		try {
			boolean isInstanceOfString = (obj instanceof String);
			if (obj != null && !isInstanceOfString) {
				Gson gson = createGson(NOT_ESCAPING_HTML, dateFormat);
				json = gson.toJson(obj);
			} else if (obj != null && isInstanceOfString) {
				json = (String) obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
			json = "";
		}
		return json;
	}
}

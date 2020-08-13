package marc.java_utilities.uri.endpoint;

import java.util.Map;
import java.util.Map.Entry;

import marc.java_utilities.strings.StringUtils;

public class Endpoint {
	
	private Endpoint() {}
	
	/**
	 * Build the endpoint with the params placed in correct site using {x}.
	 * example: build("/api/{0}/user/{1}", "param0", "param1") -- return: "/api/param0/user/param1"
	 * @param endPoint - Relative URI of endpoint
	 * @param params - Params used for build endpoint
	 * @return String - Endpoint builded
	 */
	public static String build(String endPoint, String...params) {
		return StringUtils.format(endPoint, params);
	}
	
	/**
	 * Build the endpoint with the params placed in correct site using {x}.
	 * example: build("/api/{0}/user/{1}", pathParams) -- return: "/api/param0/user/param1"
	 * @param endPoint - Relative URI of endpoint
	 * @param pathParams - Map&lt;String, String&gt; used for build endpoint
	 * @return String - Endpoint builded
	 */
	public static String build(String endPoint, Map<String, String> pathParams) {
		String uri = "";
		uri = buildUriWithPathParams(endPoint, pathParams);
		return uri;
	}
	
	private static String buildUriWithPathParams(String endPoint, Map<String, String> pathParams) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(endPoint);
		
		if (pathParams != null && !pathParams.isEmpty()) {
			
			for (Entry<String, String> param : pathParams.entrySet()) {
				String separator = "{" + param.getKey() + "}";
				int indexSeparator = builder.toString().indexOf(separator);
				
				int end = indexSeparator + separator.length();
				
				if (indexSeparator > 0) {
					builder.replace(indexSeparator, end, param.getValue());	
				}
			}

		}
		
		return builder.toString();
	}


}
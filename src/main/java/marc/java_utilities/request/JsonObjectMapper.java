package marc.java_utilities.request;

import kong.unirest.ObjectMapper;
import marc.java_utilities.json.GsonUtils;

class JsonObjectMapper implements ObjectMapper {

	public <T> T readValue(String value, Class<T> valueType) {
		return GsonUtils.returnJsonbject(value, valueType);
	}

	public String writeValue(Object value) {
		return GsonUtils.getJSON(value);
	}

}
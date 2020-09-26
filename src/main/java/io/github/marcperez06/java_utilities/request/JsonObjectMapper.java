package io.github.marcperez06.java_utilities.request;

import io.github.marcperez06.java_utilities.json.GsonUtils;
import kong.unirest.ObjectMapper;

class JsonObjectMapper implements ObjectMapper {

	public <T> T readValue(String value, Class<T> valueType) {
		return GsonUtils.returnJsonbject(value, valueType);
	}

	public String writeValue(Object value) {
		return GsonUtils.getJSON(value);
	}

}
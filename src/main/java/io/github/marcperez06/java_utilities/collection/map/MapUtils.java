package io.github.marcperez06.java_utilities.collection.map;

import java.util.Map;

public class MapUtils {
	
	private MapUtils() {}
	
	public static <T, S> boolean addObjectIfNotExistInMap(Map<T, S> map, T key, S value) {
		boolean success = false;
		if (map != null && !existObjectInMap(map, key)) {
			map.put(key, value);
			success = true;
		}
		return success;
	}
	
	public static <T, S> boolean replaceObjectInMap(Map<T, S> map, T key, S value) {
		boolean success = false;
		if (map != null) {
			map.put(key, value);
			success = true;
		}
		return success;
	}
	
	public static <T, S> boolean removeObjectInMap(Map<T, S> map, T key, S value) {
		boolean success = false;
		if (map != null && existObjectInMap(map, key)) {
			map.remove(key, value);
			success = true;
		}
		return success;
	}
	
	public static <T, S> boolean removeObjectToMap(Map<T, S> map, T key) {
		boolean success = false;
		if (map != null && existObjectInMap(map, key)) {
			map.remove(key);
			success = true;
		}
		return success;
	}
	
	public static <T, S> boolean existObjectInMap(Map<T, S> map, T key) {
		boolean exist = false;
		if (map != null && !map.isEmpty()) {
			exist = map.containsKey(key);
		}
		return exist;
	}
	
	public static <T, S> S getMapValue(Map<T, S> map, T key) {
		S mapValue = null;
		if (map != null && !map.isEmpty()) {
			mapValue = map.get(key);
		}
		return mapValue;
	}

}

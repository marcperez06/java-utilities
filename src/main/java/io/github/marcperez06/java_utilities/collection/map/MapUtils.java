package io.github.marcperez06.java_utilities.collection.map;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapUtils {
	
	private MapUtils() {}
	
	public static <K, V> boolean addObjectIfNotExistInMap(Map<K, V> map, K key, V value) {
		boolean success = false;
		if (map != null && !existObjectInMap(map, key)) {
			map.put(key, value);
			success = true;
		}
		return success;
	}
	
	public static <K, V> boolean replaceObjectInMap(Map<K, V> map, K key, V value) {
		boolean success = false;
		if (map != null) {
			map.put(key, value);
			success = true;
		}
		return success;
	}
	
	public static <K, V> boolean removeObjectInMap(Map<K, V> map, K key, V value) {
		boolean success = false;
		if (map != null && existObjectInMap(map, key)) {
			map.remove(key, value);
			success = true;
		}
		return success;
	}
	
	public static <K, V> boolean removeObjectToMap(Map<K, V> map, K key) {
		boolean success = false;
		if (map != null && existObjectInMap(map, key)) {
			map.remove(key);
			success = true;
		}
		return success;
	}
	
	public static <K, V> boolean existObjectInMap(Map<K, V> map, K key) {
		boolean exist = false;
		if (map != null && !map.isEmpty()) {
			exist = map.containsKey(key);
		}
		return exist;
	}
	
	public static <K, V> V getMapValue(Map<K, V> map, K key) {
		V mapValue = null;
		if (map != null && !map.isEmpty()) {
			mapValue = map.get(key);
		}
		return mapValue;
	}
	
	public static <K, V> Map<K, V> sortMapByKey(Map<K, V> map) {
		Map<K, V> sortedMap = null;
		if (map != null) {
			sortedMap = new TreeMap<K, V>(map);
		}
		return sortedMap;
	}
	
	public static <K, V> Map<K, V> sortMapByValue(Map<K, V> map, Comparator<V> comparator) {
		Map<K, V> sortedMap = null;
		if (map != null) {
			
			Stream<Entry<K, V>> mapStreamSorted = map.entrySet().stream().sorted(Map.Entry.comparingByValue(comparator));
			
			sortedMap = mapStreamSorted.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, 
																	(oldValue, newValue) -> oldValue, LinkedHashMap::new));
			
		}
		return sortedMap;
	}

}

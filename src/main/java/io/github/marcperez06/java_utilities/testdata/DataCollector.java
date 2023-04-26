/**
 * @author Marc Perez Rodriguez
 * Class used to store and reuse data.
 * Example:
 *  - Normally use it when I want to store data and use it in other Gherkin sentences. For Testing propose
 *  - Can be used as cache for pass data between diferent classes
 */
package io.github.marcperez06.java_utilities.testdata;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.github.marcperez06.java_utilities.logger.Logger;

public class DataCollector {

	private static Map<String, Object> data = new ConcurrentHashMap<String, Object>();
	
	private DataCollector() { }
	
	private static boolean hasValue() {
		return (data != null && !data.isEmpty());
	}
	
	private static synchronized String cleanKey(String key) {
		String cleanedKey = key;
		
		if (key != null && !key.isEmpty()) {
			
			if (key.startsWith("!")) {
				cleanedKey = key.substring(1);
			}
			cleanedKey = cleanedKey.toLowerCase();
			
		} else {
			Logger.error("Can not use an empty or null key, review your code");
		}
		
		return cleanedKey;
	}
	
	public static synchronized boolean addData(String key, Object value) {
		boolean add = true;
		if (!existData(key)) {
			setData(key, value);
		} else {
			key = createNewKey(key);
			setData(key, value);
		}
		return add;
	}
	
	public static synchronized boolean existData(String key) {
		boolean exist = false;
		if (hasValue()) {
			key = cleanKey(key);
			exist = data.containsKey(key);
		}
		return exist;
	}
	
	public static synchronized void setData(String key, Object value) {
		key = cleanKey(key);
		data.put(key, value);
		Logger.log("Added to collector ---> Key: " + key + " | Value: " + value.toString());
	}
	
	public static synchronized boolean removeData(String key) {
		boolean remove = false;
		if (existData(key)) {
			data.remove(key);
			remove = true;
		}
		return remove;
	}
	
	private static synchronized String createNewKey(String key) {
		return key + countKeyOccurences(key);
	}
	
	private static synchronized String countKeyOccurences(String key) {
		String occurence = "";
		boolean stop = false;
		int j = 0;
		
		for (int i = 0; i < data.keySet().size() && !stop; i++) {
			if (existData(key)) {
				j = (j == 0) ? 2 : j + 1;
				key = key + String.valueOf(j);
			} else {
				occurence = String.valueOf(j);
				stop = true;
			}
		}

		return occurence;
	}
	
	public static synchronized Object getData(String key) {
		Object returnValue = null;
		key = cleanKey(key);
		
		if (existData(key)) {
			returnValue = data.get(key);
		} else {
			String errorMessage = "Key does not exist in data collector: " + key;
			Logger.error(errorMessage);
			printPrettyData();
		}
		
		return returnValue;
	}
	
	public static synchronized String getStringData(String key) {
		Object obj = getData(key);
		String returnValue = (obj != null && obj instanceof String) ? String.valueOf(obj) : "";
		return returnValue;
	}
	
	public static synchronized void printData() {
		if (hasValue()) {
			Logger.forceLog("Data collector: " + data.toString());
		} else {
			Logger.forceLog("Data is empty or null");
		}
	}
	
	public static synchronized void printPrettyData() {
		if (data != null && !data.isEmpty()) {
			Logger.forceLog("Data collector:");
			for (Entry<String, Object> entry : data.entrySet()) {
				Logger.forceLog("Key: " + entry.getKey() + " | Value: " + String.valueOf(entry.getValue()));
			}
		} else {
			Logger.forceLog("Data is empty or null");
		}
	}

}
package io.github.marcperez06.java_utilities.collection.array;

import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public class ArrayUtils {

	private ArrayUtils() {}
	
	public static <T> boolean notExistObjectInArray(T[] array, T value) {
		return !existObjectInArray(array, value);
	}
	
	/**
	 * Validate if exist the object inside array
	 * @param <T> - Generic type of object
	 * @param array - Array of objects of type T
	 * @param value - Object of type T
	 * @return boolean - true if object exist in array, false otherwise
	 */
	public static <T> boolean existObjectInArray(T[] array, T value) {
		boolean exist = false;
		int i = 0;
		while (i < array.length && !exist) {
			exist = ValidationUtils.equals(value, array[i]);
			i++;
		}
		return exist;
	}
	
	public static <T> List<T> toList(T[] array) {
		List<T> list = null;
		
		if (array != null) {
			int size = array.length;
			list = new ArrayList<T>();
			for (int i = 0; i < size; i++) {
				list.add(array[i]);
			}
		}

		return list;
	}	
	
}

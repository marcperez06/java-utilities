package marc.java_utilities.collection.array;

import marc.java_utilities.validation.ValidationUtils;

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
	
	
	
}

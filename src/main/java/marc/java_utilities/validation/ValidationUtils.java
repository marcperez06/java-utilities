/**
 * @author Juan Alvarez Calixto
 * @author Marc Pérez Rodriguez
 */

package marc.java_utilities.validation;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import marc.java_utilities.collection.array.ArrayUtils;
import marc.java_utilities.reflection.ReflectionUtils;

public class ValidationUtils {

	public static final String REGEX_EMAIL = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})";
	
	private ValidationUtils() {}
	
	/**
	 * Compare two objects, and return if are equals or not
	 * @param <T> - Generic type of object
	 * @param objA - object A of type T
	 * @param objB - object B of type T
	 * @return boolean - true if are equals, false otherwise
	 */
	public static <T> boolean equals(T objA, T objB) {
		boolean areEquals = false;

		if (objA != null) {
			areEquals = equalsInherentOfClass(objA, objB);
		} else if (objB != null) {
			areEquals = equalsInherentOfClass(objB, objA);
		} else {
			areEquals = (objA == null);
			areEquals &= (objB == null);
		}
		
		return areEquals;
	}
	
	private static <T> boolean equalsInherentOfClass(T objA, T objB) {
		boolean areEquals = false;

		Class<?> clazz = null;
		Method equals = null;
		
		try {
			
			if (objA != null) {
				clazz = objA.getClass();
				equals = clazz.getDeclaredMethod("equals", clazz);
				equals.setAccessible(true);
				areEquals = (Boolean) equals.invoke(objA, objB);
			}
			
		} catch (NoSuchMethodException e) {
			areEquals = objA.equals(objB);
		} catch (Exception e) {
			areEquals = false;
		}

		return areEquals;
	}
	
	public static boolean equalsLowerCase(String a, String b) {
		String lowerA = a.toLowerCase();
		String lowerB = b.toLowerCase();
		return equals(lowerA, lowerB);
	}
	
	public static <T> boolean isNaN(T obj) {
		boolean result = true;
		try {
			if (obj instanceof Number) {
				result = false;
			}
		} catch(Exception e) {
			result = true;
		}
		return result;
	}
	
	public static <T> boolean isNotNaN(T obj) {
		return !isNaN(obj);
	}

	/**
	 * Return true if the object is null, false otherwise
	 * @param <T> - Generic type of object
	 * @param obj - Object received
	 * @return boolean - true is object is null, false otherwise
	 */
	public static <T> boolean isNull(T obj) {
		boolean result = true;
		try {
			result = (obj == null);
		} catch(Exception e) {
			result = true;
		}
		return result;
	}
	
	/**
	 * Return true if the object is not null, false otherwise
	 * @param <T> - Generic type of object
	 * @param obj - Object received
	 * @return boolean - true is object is not null, false otherwise
	 */
	public static <T> boolean isNotNull(T obj) {
		return !isNull(obj);
	}
	
	/**
	 * Return true if the object is not empty, false otherwise. Note: null is considered empty
	 * @param <T> - Generic type of object
	 * @param obj - Object received
	 * @return boolean - true is object is not empty, false otherwise
	 */
	public static <T> boolean isNotEmpty(T obj) {
		return !isEmpty(obj);
	}
	
	/**
	 * Return true if the object is empty, false otherwise. Note: null is considered empty
	 * @param <T> - Generic type of object
	 * @param obj - Object received
	 * @return boolean - true is object is empty or null, false otherwise
	 */
	public static <T> boolean isEmpty(T obj) {
		boolean isEmpty = true;
		
		try {
			
			if (obj != null) {
				if (obj.getClass().isArray() == true) {
					isEmpty = (Array.getLength(obj) == 0);
				} else if (obj instanceof List) {
					List list = (List) obj;
					isEmpty = list.isEmpty();
				} else if (obj instanceof Map) {
					Map map = (Map) obj;
					isEmpty = map.isEmpty();
				} else if (obj instanceof String) {
					String str = (String) obj;
					isEmpty = str.isEmpty();
				} else {
					isEmpty = false;
				}
			}

		} catch (Exception e) {
			isEmpty = true;
		}
		
		return isEmpty;
	}
	
	public static <T> boolean haveMinimumLength(T obj, int length) {
		boolean haveMinimum = false;
		
		try {
			if (obj instanceof String) {
				if (((String) obj).length() <= length) {
					haveMinimum = true;
				}
			} else if (obj instanceof List) {
				if (((List) obj).size() <= length) {
					haveMinimum = true;
				}
			} else if (obj instanceof Map) {
				if (((Map) obj).size() <= length) {
					haveMinimum = true;
				}
			} else if (obj instanceof Number) {
				if (((Number) obj).floatValue() <= length) {
					haveMinimum = true;
				}
			}
		} catch (Exception e) {
			haveMinimum = false;
			e.printStackTrace();
		}
		
		return haveMinimum;
	}
	
	public static <T> boolean haveMaxLength(T obj, int length) {
		boolean haveMax = false;
		
		try {
			if (obj instanceof String) {
				if (((String) obj).length() >= length) {
					haveMax = true;
				}
			} else if (obj instanceof List) {
				if (((List) obj).size() >= length) {
					haveMax = true;
				}
			} else if (obj instanceof Map) {
				if (((Map) obj).size() >= length) {
					haveMax = true;
				}
			} else if (obj instanceof Number) {
				if (((Number) obj).floatValue() >= length) {
					haveMax = true;
				}
			}
		} catch (Exception e) {
			haveMax = false;
			e.printStackTrace();
		}
		
		return haveMax;
	}
	
	public static <T> boolean isBetweenLength(T obj, int min, int max) {
		boolean isBetween = false;
		
		try {
			if (obj instanceof String) {
				
				if ( ((String) obj).length() >= min && ((String) obj).length() <= max ) {
					isBetween = true;
				}
				
			} else if (obj instanceof List) {
				
				if ( ((List) obj).size() >= min && ((List) obj).size() <= max ) {
					isBetween = true;
				}
				
			} else if (obj instanceof Map) {
				
				if ( ((Map) obj).size() >= min && ((Map) obj).size() <= max ) {
					isBetween = true;
				}
				
			} else if (obj instanceof Number) {
				
				if ( ((Number) obj).floatValue() >= min && ((Number) obj).floatValue() <= max ) {
					isBetween = true;
				}
				
			}
		} catch (Exception e) {
			isBetween = false;
			e.printStackTrace();
		}
		
		return isBetween;
	}
	
	public static boolean isPatternMatches(String pattern, String txt) {
		return Pattern.matches(pattern, txt);
	}
	
	/**
	 * Validate if the object not have the value in the field specified
	 * @param <T> - Generic type of object
	 * @param <V> - Generic type of object
	 * @param obj - Object of type T
	 * @param fieldName - String name of field
	 * @param value - Value of type V
	 * @return boolean - true if the field not have the value specified, false otherwise
	 */
	public static <T, V> boolean fieldNotHaveThisValue(T obj, String fieldName, V value) {
		boolean fieldNotHaveValue = false;
		Field field = null;

		try {
			
			if (obj != null) {

				Class<?> clazz = obj.getClass();
				field = clazz.getDeclaredField(fieldName);
				
				if (field != null) {
					field.setAccessible(true);
					fieldNotHaveValue = !equals(field.get(obj), value);
					field.setAccessible(false);
				}
				
			}
	
		} catch (Exception e) {
			fieldNotHaveValue = false;
		}
		
		return fieldNotHaveValue;
	}
	
	/**
	 * Validate if the object have the value in the field specified
	 * @param <T> - Generic type of object
	 * @param <V> - Generic type of object
	 * @param obj - Object of type T
	 * @param fieldName - String name of field
	 * @param value - Value of type V
	 * @return boolean - true if the field have the value specified, false otherwise
	 */
	public static <T, V> boolean fieldHaveThisValue(T obj, String fieldName, V value) {
		boolean fieldHaveValue = false;
		Field field = null;

		try {

			if (obj != null) {
			
				Class<?> clazz = obj.getClass();
				field = clazz.getDeclaredField(fieldName);
	
				if (field != null) {
					field.setAccessible(true);
					fieldHaveValue = equals(field.get(obj), value);
					field.setAccessible(false);
				}

			}
		
		} catch (Exception e) {
			fieldHaveValue = false;
		}
		
		return fieldHaveValue;
	}
	
	/**
	 * Validate if two objects have different values in the fieldsModified and the other fields are equals.
	 * @param <T> - Generic type of object
	 * @param objA - Object A of type T
	 * @param objB - Object B of type T
	 * @param fieldsModified - Array of String fields that are modified
	 * @return boolean - true if only the fields specified are modified, false otherwise
	 */
	public static <T> boolean modificationsAreCorrect(T objA, T objB, String...fieldsModified) {
		boolean modificationsAreCorrect = false;
		boolean fieldError = false;

		try {
		
			if (canVerifyTheModifications(objA, objB, fieldsModified) == true) {
			
				Class<?> clazz = objA.getClass();
				Field[] fields = clazz.getDeclaredFields();
				
				for (int i = 0; i < fields.length && fieldError == false; i++) {
					Field field = fields[i];
					String fieldName = field.getName();
					field.setAccessible(true);
					
					if (ArrayUtils.existObjectInArray(fieldsModified, fieldName)) {

						fieldError = fieldValueIsEqual(field, objA, objB);
						
					} else {
						fieldError = fieldValueIsNotEqual(field, objA, objB);
					}
					
					field.setAccessible(false);
				}
				
				if (fieldError == false) {
					modificationsAreCorrect = true;
				}
				
			}

		} catch (Exception e) {
			modificationsAreCorrect = false;
		}
		
		return modificationsAreCorrect;
	}

	private static <T> boolean canVerifyTheModifications(T objA, T objB, String[] fieldsModified) {
		boolean canVerifyTheModifications = (fieldsModified != null);
		canVerifyTheModifications &= (fieldsModified.length > 0);
		canVerifyTheModifications &= (objA != null);
		canVerifyTheModifications &= (objB != null);
		return canVerifyTheModifications;
	}

	/**
	 * Validate if exist the object inside array
	 * @param <T> - Generic type of object
	 * @param array - Array of objects of type T
	 * @param value - Object of type T
	 * @return boolean - true if object exist in array, false otherwise
	 */
	/*
	public static <T> boolean existObjectInArray(T[] array, T value) {
		boolean exist = false;
		if (array != null) {
			for (int i = 0; i < array.length && exist == false; i++) {
				exist = equals(array[i], value);
			}
		}
		return exist;
	}
	*/

	private static <T> boolean fieldValueIsNotEqual(Field field, T objA, T objB) {
		return !fieldValueIsEqual(field, objA, objB);
	}
	
	private static <T> boolean fieldValueIsEqual(Field field, T objA, T objB) {
		boolean isEqual = false;
		try {
			isEqual = equals(field.get(objA), field.get(objB));
		} catch (IllegalArgumentException e) {
			isEqual = false;
		} catch (IllegalAccessException e) {
			isEqual = false;
		}
		return isEqual;
	}
	
	/**
	 * Validate if exist the object inside list
	 * @param <T> - Generic type of object
	 * @param list - List of objects of type T
	 * @param value - Object of type T
	 * @return boolean - true if object exist in list, false otherwise
	 */
	/*
	public static <T> boolean existObjectInList(List<T> list, T value) {
		boolean exist = false;
		for (int i = 0; i < list.size() && exist == false; i++) {
			if (equals(list.get(i), value) == true) {
				exist = true;
			}
		}
		return exist;
	}
	*/
	
	/**
	 * Validate if exist the key inside map.
	 * @param <T> - Generic type of object
	 * @param <S> - Generic type of object
	 * @param map - Map&lt;T, S&gt;
	 * @param key - Key inside map of type T
	 * @return boolean - true if the map contains the key, false otherwise
	 */
	/*
	public static <T, S> boolean existObjectInMap(Map<T, S> map, T key) {
		return map.containsKey(key);
	}
	*/

	/**
	 * Return true if the object is of the type specified, false otherwise
	 * @param obj Object received
	 * @param type Type of class specified
	 * @return boolean that is 'true' is object belongs to class, 'false' otherwise
	 */
	public static <T, X> boolean objectIsOfType(T obj, Class<X> type) {
		boolean isOfType = false;

		try {
			isOfType = type.isInstance(obj);	
		} catch (Exception e) {
			isOfType = false;
		}

		return isOfType;
	}
	
	/**
	 * Return true if all the objects inside list are of the type specified, false othersiwe
	 * @param objects List of objects received
	 * @param type Type of class specified
	 * @return boolean that is 'true' is all objects belongs to class, 'false' otherwise
	 */
	public static <T, X> boolean listOfObjectAreOfType(List<T> objects, Class<X> type) {
		boolean areOfType = false;
		
		try {
			
			for (T obj : objects) {
				areOfType = type.isInstance(obj);
			}

		} catch (Exception e) {
			areOfType = false;
		}

		return areOfType;
	}
	
	/**
	 * Return true if the 2 objects have same properties, ignoring the properties of object A that are null, false otherwise
	 * @param <T> - Object of any type
	 * @param <X> - Object of any type
	 * @param objA - Object A of type T
	 * @param objB - Object B of type X
	 * @return boolean - true if objects have the same properties, ignoring Null values, false otherwise
	 */
	public static <T, X> boolean objectAHaveSamePropertiesObjectB(T objA, X objB) {
		boolean haveSameProperties = false;
		
		try {

			boolean areNotNull = (objA != null && objB != null);
			
			if (areNotNull) {
				List<Field> objAFields = ReflectionUtils.getAllFieldsInObject(objA);

				haveSameProperties = !objAFields.isEmpty();
				
				for (int i = 0; i < objAFields.size() && haveSameProperties; i++) {
					Field fieldOfObjA = objAFields.get(i);
					haveSameProperties &= fieldHaveSameValueIgnoringNulls(fieldOfObjA, objA, objB);
				}
				
			} else {
				haveSameProperties = true;
			}

		} catch (Exception e) {
			haveSameProperties = false;
		}
		
		return haveSameProperties;
	}
	
	private static <T, X> boolean fieldHaveSameValueIgnoringNulls(Field field, T objA, X objB) {
		boolean haveSameValue = false;
		field.setAccessible(true);
		
		try {	
			if (field.get(objA) != null) {
				haveSameValue = fieldValueIsEqual(field, objA, objB);
			} else {
				haveSameValue = true;
			}
		} catch (Exception e) {
			haveSameValue = false;
		}
		
		field.setAccessible(false);
		return haveSameValue;
	}
	
	public static <T> boolean objPropertiesNotHaveValue(T obj) {
		boolean isEmpty = false;
		
		try{
			Class<?> clazz = Class.forName(obj.getClass().getName());
			Field[] fields = clazz.getDeclaredFields();

			for (int i = 0; i < fields.length && !isEmpty; i++) {
				
				Field field = fields[i];
				
				if (field.get(obj) instanceof String
						|| field.get(obj) instanceof List || field.get(obj) instanceof Map) {
					
					isEmpty = isEmpty(obj);

				} else if (isNull(field.get(obj))) {
					isEmpty = true;
				}
			}

		} catch(Exception e) {
			isEmpty = true;
		}
		
		return isEmpty;
	}

}
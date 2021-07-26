package io.github.marcperez06.java_utilities.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.internal.LinkedTreeMap;

public class ReflectionUtils {
	
	private ReflectionUtils() {}
	
	/**
	 * Return object based on map.
	 * @param <T> - Generic param for class
	 * @param <K> - Generic param for map key
	 * @param <V> - Generic param for map value
	 * @param map - Map that contains keys and values of object
	 * @param type - Class of object to return
	 * @return T - Object of type specified
	 */
	public static <T, K, V> T getSimpleObjectByMap(Map<K, V> map, Class<T> type) {
		T obj = null;
		List<T> list = getListOfSimpleObjectByMap(map, type);
		
		if (list.isEmpty() == false) {
			obj = list.get(0);
		}

		return obj;
	}
	
	/**
	 * Return list of objects based on map.
	 * @param <T> - Generic param for class
	 * @param <K> - Generic param for map key
	 * @param <V> - Generic param for map value
	 * @param map - Map that contains keys and values of objects
	 * @param type - Class of objects to return
	 * @return List - List of objects of type specified
	 */
	public static <T, K, V> List<T> getListOfSimpleObjectByMap(Map<K, V> map, Class<T> type) {
		List<T> list = new ArrayList<T>();
		int size = getMaxSizeOfValueListByMap(map);
		int count = 0;

		try {

			while (count < size) {
				
				T obj = createSimpleObjectByMap(map, type, count);
				
				if (obj != null) {
					list.add(obj);
				}
				
				count++;
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	private static <K, V> int getMaxSizeOfValueListByMap(Map<K, V> map) {
		int size = 0;
		if (map != null && map.isEmpty() == false) {
			for (Entry<K, V> mapEntry : map.entrySet()) {
				
				int currentSize = getCurrentSizeOfValueList(mapEntry.getValue());
				
				if (currentSize > size) {
					size = currentSize;
				}
			}
		}
		return size;
	}
	
	@SuppressWarnings("unchecked")
	private static <T, V> int getCurrentSizeOfValueList(T mapValue) {
		int currentSize = 0;
		
		if (mapValue instanceof List) {
			
			List<V> listOfValues = (List<V>) mapValue;
			
			if (listOfValues != null && listOfValues.isEmpty() == false) {
				
				V auxVal = listOfValues.get(0);
				
				if (auxVal instanceof LinkedHashMap || auxVal instanceof LinkedTreeMap) {
					currentSize = 1;
				} else {
					currentSize = listOfValues.size();
				}

			}
			
		} else if (mapValue != null) {
			currentSize = 1;
		}

		return currentSize;
	}
	
	@SuppressWarnings("unchecked")
	private static <T, K, V> T createSimpleObjectByMap(Map<K, V> map, Class<T> type, int count) {
		T obj = null;
		int index = count;
		
		try {
			List<Field> fields = getAllFieldsInClass(type);
			Constructor<T> constructor = type.getConstructor();
			obj = constructor.newInstance();
			
			for (Field field : fields) {
				field.setAccessible(true);
				
				if (!Modifier.isFinal(field.getModifiers())) {
					K key = (K) field.getName();
					List<V> listOfValues = new ArrayList<V>();
					
					if (map.get(key) instanceof List) {
						listOfValues = (List<V>) map.get(key);
						index = count;
					} else {
						V val = (V) map.get(key);
						listOfValues.add(val);
						index = 0;
					}
					
					fillFieldWithValues(obj, field, listOfValues, index);
				}

			}
			
		} catch (Exception e) {
			obj = null;
			e.printStackTrace();
		}
		
		return obj;
	}
	
	/**
	 * Fill the field inside object with the first value of list.
	 * @param <T> - Generic param for object
	 * @param <V> - Generic param for values
	 * @param obj - Object wished to put value
	 * @param field - Field where the value is put
	 * @param listOfValues - List of values
	 */
	public static <T, V> void fillFieldWithValues(T obj, Field field, List<V> listOfValues) {
		fillFieldWithValues(obj, field, listOfValues, 0);
	}
	
	private static <T, V> void fillFieldWithValues(T obj, Field field, List<V> listOfValues, int index) {
		V value = null;
		
		try {

			if (listOfValues != null && listOfValues.isEmpty() == false && index < listOfValues.size()) {
				value = listOfValues.get(index);
			}

			if (field.getType().isAssignableFrom(List.class) == true && value instanceof Map) {
				fillFieldWithListOfLinkedHashMap(field, obj, listOfValues);
			} else if (field.getType().isAssignableFrom(Map.class) == true && value instanceof Map) {
				Map map = (Map) value;
				field.set(obj, map);
			} else if (field.getType().isAssignableFrom(List.class) == true) {
				field.set(obj, listOfValues);
			} else if (value instanceof Map) {
				fillFieldWithObjcetOfLinkedHashMap(field, obj, listOfValues);
			} else {
				fillFieldWithValue(obj, field, value);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T, V> void fillFieldWithListOfLinkedHashMap(Field field, T obj, List<V> listOfValues) {
		try {
			Class<?> typeOfClass = getGenericTypeOfField(field);
			
			List list = new ArrayList();
			
			for (V mapValue : listOfValues) {
				Map auxMap = (Map) mapValue;
				V simpleObj = (V) getSimpleObjectByMap(auxMap, typeOfClass);
				list.add(simpleObj);
			}
			
			field.set(obj, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T, V> void fillFieldWithObjcetOfLinkedHashMap(Field field, T obj, List<V> listOfValues) {
		try {
			Class<?> typeOfClass = getGenericTypeOfField(field);
			Map auxMap = (Map) listOfValues.get(0);
			V simpleObj = (V) getSimpleObjectByMap(auxMap, typeOfClass);
			field.set(obj, simpleObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fill the field inside object with the value.
	 * @param <T> - Generic param for object
	 * @param <V> - Generic param for values
	 * @param obj - Object wished to put value
	 * @param field - Field where the value is put
	 * @param value - Value that you want to put in field
	 */
	public static <T, V> void fillFieldWithValue(T obj, Field field, V value) {
		
		if (!field.canAccess(obj)) {
			setAccessible(field, true);
		}

		if (field.canAccess(obj))  {
			if (field.getType().isPrimitive() == true) {
				fillFieldWithPrimitiveValue(obj, field, value);
			} else {
				fillFieldWithoutPrimitiveValue(obj, field, value);
			}
		} else {
			String message = "Can not fill the value " + String.valueOf(value);
			message += " in the field " + field.getName() + " of object " + obj.toString();
			System.out.println(message);
		}
		
	}
	
	private static <T, V> void fillFieldWithPrimitiveValue(T obj, Field field, V value) {
		String strValue = "";
		
		if (value != null) {
			strValue = String.valueOf(value);
		}

		try {

			if (value == null) {
				
				if (field.getType().isAssignableFrom(Number.class) == true) {
					field.set(obj, 0);
				} else if (field.getType().isAssignableFrom(Boolean.class) == true) {
					field.set(obj, false);
				}

			} else  {
				
				if (field.getType().isAssignableFrom(int.class)) {
					String[] splitValue = strValue.split("\\.");
					String intValue = splitValue[0];
					field.set(obj, Integer.valueOf(intValue));
				} else if (field.getType().isAssignableFrom(float.class)) {
					field.set(obj, Float.valueOf(strValue).floatValue());
				} else if (field.getType().isAssignableFrom(double.class)) {
					field.set(obj, Double.valueOf(strValue).doubleValue());
				} else if (field.getType().isAssignableFrom(long.class)) {
					field.set(obj, Long.valueOf(strValue).longValue());
				} else if (field.getType().isAssignableFrom(short.class)) {
					field.set(obj, Short.valueOf(strValue).shortValue());
				} else if (field.getType().isAssignableFrom(boolean.class)) {
					field.set(obj, Boolean.valueOf(strValue).booleanValue());
				}
				
			}
	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static <T, V> void fillFieldWithoutPrimitiveValue(T obj, Field field, V value) {
		String strValue = "";
		
		if (value != null) {
			strValue = String.valueOf(value);
		}
		
		try {
			
			if (value == null) {
				field.set(obj, value);
			} else if (field.getType().isAssignableFrom(String.class)) {
				field.set(obj, String.valueOf(value));
			} else if (field.getType().isAssignableFrom(Integer.class)) {
				String[] splitValue = strValue.split("\\.");
				String intValue = splitValue[0];
				field.set(obj, Integer.valueOf(intValue));
			} else if (field.getType().isAssignableFrom(Float.class)) {
				field.set(obj, Float.valueOf(strValue));
			} else if (field.getType().isAssignableFrom(Double.class)) {
				field.set(obj, Double.valueOf(strValue));
			} else if (field.getType().isAssignableFrom(Long.class)) {
				field.set(obj, Long.valueOf(strValue));
			} else if (field.getType().isAssignableFrom(Boolean.class)) {
				field.set(obj, Boolean.valueOf(strValue));
			} else if (field.getType().isAssignableFrom(URI.class)) {
				field.set(obj, URI.create(strValue));
			} else {
				field.set(obj, value);
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the generic type name specified in a field.
	 * @param field - Field
	 * @return String - Class name used in field, it have gerenric type parameter, 
	 * 						are written inside &lt;&gt; (Example: List&lt;String&gt;)
	 */
	public static String getGenericClassNameOfField(Field field) {
		String className = "Not have class";
		Class<?> typeOfClass = getGenericTypeOfField(field);
		
		if (typeOfClass != null) {
			className = typeOfClass.getSimpleName();
		}
		
		return className;
	}
	
	/**
	 * Return the generic type specified in a field.
	 * @param field - Field
	 * @return Class - Class used in field which is generic type
	 */
	public static Class<?> getGenericTypeOfField(Field field) {
		Class<?> genericType = null;
		List<Class<?>> listOfTypes = getGenericTypesOfField(field);
		if (!listOfTypes.isEmpty()) {
			genericType = listOfTypes.get(0);
		}
		return genericType;
	}
	
	/**
	 * Return the generic types specified in a field.
	 * @param field - Field
	 * @return List&lt;Class&gt; - List of class used in field which is generic type
	 */
	public static List<Class<?>> getGenericTypesOfField(Field field) {
		List<Class<?>> listOfTypes = new ArrayList<Class<?>>();
		
		if (field != null) {

			Class<?> typeOfClass = field.getType();
			
			try {
				
				Type fieldType = field.getGenericType();
				
				if (fieldType instanceof ParameterizedType) {
					
					ParameterizedType genericType = (ParameterizedType) fieldType;
					
					Class<?> actualClassArgument = getGenericClassOfParameterizedType(genericType);
					
					if (actualClassArgument != null) {
						listOfTypes.add(actualClassArgument);
					}

				} else {
					listOfTypes.add(typeOfClass);
				}

			} catch (Exception e) {
				System.out.println("Field --> " + field.getName());
				e.printStackTrace();
			}
			
		}

		return listOfTypes;
	}
	
	private static Class<?> getGenericClassOfParameterizedType(ParameterizedType parameterizedType) {
		Class<?> genericClass = null;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		
		try {
			for (int i = 0; i < actualTypeArguments.length; i++) {
				if (!(actualTypeArguments[i] instanceof ParameterizedType)) {
					String className = actualTypeArguments[i].getTypeName();
					genericClass = Class.forName(className);
				} else {
					ParameterizedType auxParameterizedType = (ParameterizedType) actualTypeArguments[i];
					genericClass = getGenericClassOfParameterizedType(auxParameterizedType);
				}
			}
		} catch (Exception e) {
			
		}

		return genericClass;
	}
	
	/**
	 * Return the generic parameter type of class who's parent extends a Class&lt;T&gt;
	 * @param genericClass - Class who's parents extends the generic parameter
	 * @return Class - Class of the generic parameter of parent
	 */
	public static Class<?> getGenericParameterOfClass(Class<?> genericClass) {
		Class<?> genericParameter = null;
		List<Class<?>> listOfTypes = getGenericParametersOfClass(genericClass);
		if (!listOfTypes.isEmpty()) {
			genericParameter = listOfTypes.get(0);
		}
		return genericParameter;
	}
	
	/**
	 * Return the generic parameters type of class who's parent extends a Class&lt;T, S, U&gt;
	 * @param genericClass - Class who's parents extends the generic parameter
	 * @return Class - Class of the generic parameter of parent
	 */
	public static List<Class<?>> getGenericParametersOfClass(Class<?> genericClass) {
		List<Class<?>> listOfTypes = new ArrayList<Class<?>>();
		Field fieldParameterizedType = getField("parameterizedType", genericClass);
		Class<?> parameterizedTypesOfClass = getGenericTypeOfField(fieldParameterizedType);
		Type type = (Type) genericClass;
		
		if (type != null) {
			
			if (type instanceof ParameterizedType) {
	            ParameterizedType parameterizedType = (ParameterizedType) type;
	    		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
	    		//TypeVariable<?>[] typesOfClass = genericClass.getTypeParameters();
	    		
	    		if (actualTypeArguments.length > 0) {
	    			
	    			for (int i = 0; i < actualTypeArguments.length; i++) {
	    				
	    				try {
	    					String className = actualTypeArguments[i].getTypeName();
							Class<?> actualClassParameter = Class.forName(className);
		    				listOfTypes.add(actualClassParameter);	
	    				} catch (Exception e) {
	    					// Empty Exception
	    				}
	    				
	    			}

	    		}

	        } else {
	        	Class<?> superClass = (Class<?>) genericClass.getGenericSuperclass();
	        	listOfTypes = getGenericParametersOfClass(superClass);
	        }
			
		} else {
			listOfTypes.add(parameterizedTypesOfClass);
		}

		return listOfTypes;
	}
	
	/**
	 * Set if object is accessible or not
	 * @param accessibleObject - AccessibleObject (Field, Method, Member, etc...)
	 * @param accessible - boolean
	 */
	public static void setAccessible(AccessibleObject accessibleObject, final boolean accessible) {
		if (accessibleObject != null) {
			try {
				accessibleObject.setAccessible(accessible);
			} catch (Exception e) {
				if (accessible) {
					accessibleObject.trySetAccessible();
				}
			}
		}
	}
	
	/**
	 * Returns the field of object or parent object by name of field.
	 * @param <T> - Generic param for object
	 * @param fieldName - Name of field
	 * @param obj - Object
	 * @return Field - Field if it have, null otherwise
	 */
	public static <T> Field getField(String fieldName, T obj) {
		Field field = (obj != null) ? getField(fieldName, obj.getClass()) : null;
		return field;
	}
	
	/**
	 * Returns the field of class or parent class by name of field.
	 * @param <T> - Generic param for class
	 * @param fieldName - Name of field
	 * @param clazz - Class that have the field
	 * @return Field - Field if it have, null otherwise
	 */
	public static <T> Field getField(String fieldName, Class<T> clazz) {
		Field field = null;
		
		if (clazz != null) {
		
			try {
				field = getDeclaredField(fieldName, clazz);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				setAccessible(field, true);
			}
			
		}
		
		return field;
	}
	
	private static <T> Field getDeclaredField(String fieldName, Class<T> clazz) {
		Field field = null;
		
		try {

			boolean isNotObjectClass = (clazz.getName().equals("java.lang.Object") == false);
			
			if (isNotObjectClass) {
				
				List<Field> fields = getAllFieldsInClass(clazz);
				field = findObjectInList(fields, "name", fieldName);
	
			} else {
				field = null;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			field = null;
		}
		
		return field;
	}
	
	/**
	 * Returns all fields inside object (only his fields).
	 * @param <T> - Generic param for object
	 * @param obj - Object
	 * @return List - List of Fields, that contains all fields in object.
	 */
	public static <T> List<Field> getFieldsOfObject(T obj) {
		List<Field> fields = new ArrayList<Field>();
		if (obj != null) {
			fields = getFieldsOfClass(obj.getClass());
		}
		return fields;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Field> getFieldsOfClass(Class clazz) {
		ArrayList<Field> fields = new ArrayList<Field>();
		
		try {
			Field[] classFields = clazz.getDeclaredFields();
			fields.addAll(Arrays.asList(classFields));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
	/**
	 * Returns all fields inside object (his fields and parent fields).
	 * @param <T> - Generic param for object
	 * @param obj - Object
	 * @return List - List of Fields, that contains all fields in object (including parent fields).
	 */
	public static <T> List<Field> getAllFieldsInObject(T obj) {
		List<Field> fields = new ArrayList<Field>();
		if (obj != null) {
			fields = getAllFieldsInClass(obj.getClass());
		}
		return fields;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Field> getAllFieldsInClass(Class clazz) {
		List<Field> fields = new ArrayList<Field>();
		
		try {
			
			Class parentClass = clazz.getSuperclass();

			while (parentClass != null && !parentClass.getName().equals("java.lang.Object")) {
				fields.addAll(getFieldsOfClass(parentClass));
				parentClass = parentClass.getSuperclass();
			}
			
			fields.addAll(getFieldsOfClass(clazz));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
	/**
	 * Return the value in field name, in a Generic type
	 * @param <T> - Generic type of Object
	 * @param <V> - Generic type of Object
	 * @param object - Object
	 * @param fieldName - String name of field that get value
	 * @return V - value of field
	 */
	@SuppressWarnings("unchecked")
	public static <T, V> V getFieldValue(T object, String fieldName) {
		V value = null;
		Field field = getField(fieldName, object);
		
		if (field != null) {
			try {
				if (field.canAccess(field)) {
					value = (V) field.get(object);
				}			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return value;
	}
	
	/**
	 * Return all the fields in class that have the annotation specified
	 * @param clazz - Class where try to found fields with the annotation
	 * @param annotationClass - Class of annotation
	 * @return List&lt;Field&gt; - List with all the fields with the annotation specified, if not fields found, return empty list
	 */
	public static List<Field> getAnnotatedDeclaredFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> allFields = getFieldsOfClass(clazz);
        List<Field> annotatedFields = new ArrayList<Field>();
        
        if (allFields != null) {
        
        	for (Field field : allFields) {
        		if (field.isAnnotationPresent(annotationClass)) {
                    annotatedFields.add(field);
                }
            }
        	
        }

        return annotatedFields;
    }
	
	/**
	 * Return the object inside list if the fieldName have the corresponding value, null otherwise.
	 * @param <T> - Generic param for objects inside list
	 * @param <V> - Generic param for value
	 * @param list - List of objects
	 * @param fieldName - Name of field
	 * @param value - Value of field
	 * @return T - Object if found it, null otherwise
	 */
	public static <T, V> T findObjectInList(List<T> list, String fieldName, V value) {
		T obj = null;
		boolean found = false;
		
		try {
			
			if (list != null) {
				
				for (int i = 0; i < list.size() && !found; i++) {
					T object = list.get(i);
					Field field = getField(fieldName, object.getClass());
					
					if (field != null) {
						
						found = field.get(object).equals(value);
						
						if (found) {
							obj = object;
						}
						
					}
				}
				
			}

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return obj;
	}
	
	/**
	 * Return the object inside array if the fieldName have the corresponding value, null otherwise.
	 * @param <T> - Generic param for objects inside array
	 * @param <V> - Generic param for value
	 * @param array - Array of objects
	 * @param fieldName - Name of field
	 * @param value - Value of field
	 * @return T - Object if found it, null otherwise
	 */
	public static <T, V> T findObjectInArray(T[] array, String fieldName, V value) {
		T obj = null;
		boolean found = false;
		
		try {
			
			if (array != null) {
			
				for (int i = 0; i < array.length && !found; i++) {
					T object = array[i];
					Field field = getField(fieldName, object.getClass());
					
					if (field != null) {
						
						found = field.get(object).equals(value);
						
						if (found) {
							obj = object;
						}
						
					}
				}
				
			}
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return obj;
	}
	
	/**
	 * Return a list of objects that the fieldName have the corresponding value, empty list otherwise.
	 * @param <T> - Generic param for objects inside list
	 * @param <V> - Generic param for value
	 * @param list - List of objects
	 * @param fieldName - Name of field
	 * @param value - Value of field
	 * @return List - List of Object if found any, empty List otherwise
	 */
	public static <T, V> List<T> findObjectsInList(List<T> list, String fieldName, V value) {
		List<T> foundObjects = new ArrayList<T>();
		boolean found = false;
		
		try {
			
			if (list != null) {
			
				for (int i = 0; i < list.size(); i++) {
					T object = list.get(i);
					Field field = getField(fieldName, object.getClass());
					
					if (field != null) {
						
						found = field.get(object).equals(value);
						
						if (found) {
							foundObjects.add(object);
						}

					}
				}
				
			}
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return foundObjects;
	}
	
	/**
	 * Return a list of objects that the fieldName have the corresponding value, empty list otherwise.
	 * @param <T> - Generic param for objects inside array
	 * @param <V> - Generic param for value
	 * @param array - Array of objects
	 * @param fieldName - Name of field
	 * @param value - Value of field
	 * @return List - List of Object if found any, empty List otherwise
	 */
	public static <T, V> List<T> findObjectsInArray(T[] array, String fieldName, V value) {
		List<T> foundObjects = new ArrayList<T>();
		boolean found = false;
		
		try {
			
			if (array != null) {
			
				for (int i = 0; i < array.length; i++) {
					T object = array[i];
					Field field = object.getClass().getField(fieldName);
					
					if (field != null) {

						found = field.get(object).equals(value);
						
						if (found) {
							foundObjects.add(object);
						}	
					}

				}

			}
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return foundObjects;
	}
	
	/**
	 * Clone the object received by param.
	 * @param <T> - Generic param for object
	 * @param obj - Object
	 * @return T - Clone of object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T cloneObject(T obj) {
		T clone = null;
		
		if (obj != null) {
		
			try {
	
				Class clazz = obj.getClass();
				Constructor<T> constructor = clazz.getConstructor();
				clone = constructor.newInstance();
				
				List<Field> fields = getAllFieldsInClass(clazz);
				
				for (Field field : fields) {
					field.setAccessible(true);
					fillFieldWithValue(clone, field, field.get(obj));
					field.setAccessible(false);
				}
				
			} catch (Exception e) {
				clone = null;
			}
		
		}

		return clone;
	}
	
	/**
	 * Returns boolean that indicates if the objects have the same properties with values.
	 * @param <T> - Generic param for object A and B
	 * @param objA - Object A
	 * @param objB - Object B
	 * @return boolean - true if objects have the same properties with values, false otherwise.
	 */
	public static <T> boolean haveSameProperties(T objA, T objB) {
		boolean haveSameProperties = false;
		
		haveSameProperties = (objA == null);
		haveSameProperties &= (objB == null);
		
		if (haveSameProperties == false) {
			haveSameProperties = allFieldsAreEquals(objA, objB);
		}

		return haveSameProperties;
	}
	
	private static <T> boolean allFieldsAreEquals(T objA, T objB) {
		boolean allFieldsAreEquals = false;
		boolean notAreNull = (objA != null && objB != null);
		
		if (notAreNull == true) {
		
			allFieldsAreEquals = true;

			Field[] fields = objA.getClass().getDeclaredFields();
			
			for (int i = 0; i < fields.length && allFieldsAreEquals == true; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				allFieldsAreEquals = fieldAreEquals(field, objA, objB);
			}
			
		}
		
		return allFieldsAreEquals;
	}
	
	private static <T> boolean fieldAreEquals(Field field, T objA, T objB) {
		boolean areEqual = false;
		
		try {
			if (field.get(objA) != null) {
				areEqual = field.get(objA).equals(field.get(objB));
			} else if (field.get(objB) != null) {
				areEqual = field.get(objB).equals(field.get(objA));
			} else {
				areEqual = (field.get(objA) == null);
				areEqual &= (field.get(objB) == null);
			}
		} catch (IllegalArgumentException e) {
			areEqual = false;
		} catch (IllegalAccessException e) {
			areEqual = false;
		}
		
		return areEqual;
	}

	/**
	 * Return Map of fields with his value in type of Object.class.
	 * @param <T> - Generic param for object
	 * @param obj - Object
	 * @return Map - Map where the key is String and the value is an Object 
	 */
	public static <T> Map<String, Object> getMapFromObject(T obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Field> allFields = getAllFieldsInObject(obj);
		
		for (Field field : allFields) {
			
			field.setAccessible(true);

			try {
				map.put(field.getName(), field.get(obj));
			} catch (IllegalArgumentException e) {
				map.put(field.getName(), null);
			} catch (IllegalAccessException e) {
				map.put(field.getName(), null);
			}
			
		}
		
		return map;
	}
	
	/**
	 * Transform Object specified by param in a new Object of Class specified by param 
	 * @param <T> - Generic param for object
	 * @param <O> - Generic Class Of object
	 * @param objectA - Object to transform
	 * @param classB - Class of new object returned
	 * @return O - New object of class specified by param 
	 */
	public static <T, O> O transform(T objectA, Class<O> classB) {
		O obj = null;
		try {
			Constructor<O> constructor = classB.getConstructor();
			obj = constructor.newInstance();
			
			if (objectA != null) {
				
				List<Field> fieldsObjectA = getAllFieldsInObject(objectA);
				
				for (Field fieldA : fieldsObjectA) {
					fillTheSameFieldOfDifferentObjects(fieldA, objectA, obj);
				}

			}
			
		} catch (Exception e) {
			//Logger.println("Object can not be transform to Class specified, null will be returned");
			e.printStackTrace();
			obj = null;
		}
		
		return obj;
	}
	
	private static <A, B> void fillTheSameFieldOfDifferentObjects(Field fieldA, A objectA, B objectB) throws Exception {
		String fieldAName = fieldA.getName();
		Class<?> classFieldA = getGenericTypeOfField(fieldA);
		
		fieldA.setAccessible(true);
		
		Field fieldB = getField(fieldAName, objectB);
		
		if (fieldB != null) {
			
			String fieldBName = fieldB.getName();
			Class<?> classFieldB = getGenericTypeOfField(fieldB);
			
			boolean isTheSameField = fieldAName.equals(fieldBName);
			isTheSameField &= classFieldA.equals(classFieldB);
			
			if (isTheSameField) {
				fillFieldWithValue(objectB, fieldB, fieldA.get(objectA));
			}

		}
	}
	
	// ******************** GET METHODS ***********************************
	public static <T> Method getMethod(T object, String name, Class<?>...parameterTypes) {
		Method method = null;
		if (object != null) {
			Class<?> clazz = object.getClass();
			method = getMethod(clazz, name, parameterTypes);
		}
		return method;
	}
	
	public static <T> Method getMethod(Class<T> clazz, String name, Class<?>...parameterTypes) {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
		} catch (Exception e) {
			method = null;
			e.printStackTrace();
		}
		return method;
	}
	
	public static <T> List<Method> getAllMethods(T object) {
		List<Method> methods = new ArrayList<Method>();
		if (object != null) {
			Class<?> clazz = object.getClass();
			methods = getAllMethods(clazz);
		}
		return methods;
	}
	
	public static <T> List<Method> getAllMethods(Class<T> clazz) {
		List<Method> methods = new ArrayList<Method>();
		
		try {
			
			Class<?> parentClass = clazz.getSuperclass();

			while (parentClass != null && !parentClass.getName().equals("java.lang.Object")) {
				methods.addAll(getMethodsOfClass(parentClass));
				parentClass = parentClass.getSuperclass();
			}
			
			methods.addAll(getMethodsOfClass(clazz));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return methods;	
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Method> getMethodsOfClass(Class clazz) {
		List<Method> methods = new ArrayList<Method>();
		
		try {
			Method[] classMethods = clazz.getDeclaredMethods();
			methods.addAll(Arrays.asList(classMethods));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return methods;
	}
	
	public static <T> void printObjectProperties(T object) {
		
		if (object != null) {
			
			try {
				
				Class<T> clazz = (Class<T>) object.getClass();
				String className = clazz.getCanonicalName();
				
				if (!clazz.isPrimitive() && !clazz.getName().equals("java.lang.Object")) {
					
					System.out.println("-------------------------------------------------------------------------");
					System.out.println("Class Name: " + className);
					System.out.println("Class Package: " + clazz.getPackage().getName());
					System.out.println("------------------------- " + className + " Properties ----------------------------");
					
					List<Field> fields = getAllFieldsInClass(clazz);
					
					for (Field field : fields) {
						
						if (field != null) {
							field.setAccessible(true);
							String fieldName = field.getName();
							Class<?> fieldClass = getGenericTypeOfField(field);
							Package packageObj = fieldClass.getPackage();
							String packageName = (packageObj != null) ? packageObj.getName() : "None";
							Object fieldValue = field.get(object);
							
							if (fieldValue != null) {
								System.out.println("-------------------------------------------------------------------------");
								System.out.println("Field Name: " + fieldName);
								System.out.println("Field Class: " + fieldClass.getCanonicalName());
								System.out.println("Field Class Package: " + packageName);
								
								if (String.class.isInstance(fieldValue)
										|| Number.class.isInstance(fieldValue)
										|| Map.class.isInstance(fieldValue)
										|| fieldClass.isArray()) {

									System.out.println("Field Value: " + String.valueOf(fieldValue));
								} else if (!clazz.isPrimitive()){
									printObjectProperties(fieldValue);
								}
								
								System.out.println("-------------------------------------------------------------------------");
							}
							
						}
						
					}
					
				} else {
					System.out.println("-------------------------------------------------------------------------");
					System.out.println("Is a primitive class --> " + className);
					System.out.println("-------------------------------------------------------------------------");
				}

				System.out.println("-------------------------------------------------------------------------");
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Object you want info is NULL");
		}

	}

}
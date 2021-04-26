/**
 * Classe Estatica con utilidades para rellenar clases con los resultados de la base de datos.
 * @author Marc Perez Rodriguez
 */

package io.github.marcperez06.java_utilities.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;

public class DatabaseUtils {
	
	public static <T> List<T> getListFromResultSet(ResultSet resultSet, Class<T> genericClass) {
		List<T> listFilled = new ArrayList<T>();

		try {
			
			if (resultSet != null && resultSet.isClosed() == false) {
			
				while(resultSet.next()) {
					resultSet.previous();
					Constructor<T> constructor = genericClass.getConstructor();
					T obj = constructor.newInstance();
					fillFromResultSet(resultSet, obj);
					listFilled.add(obj);
				}
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return listFilled;
	}
	
	public static <T> boolean fillFromResultSet(ResultSet rs, T obj) {
		boolean filled = true;
		
		try {
			Class<?> clazz = Class.forName(obj.getClass().getName());
			
			List<Field> fields = ReflectionUtils.getAllFieldsInClass(clazz);
			
			if(rs != null && rs.next() == true) {
			
				for(Field field : fields){
					
					if (field != null) {
						field.setAccessible(true);						
						setFieldValueFromResultSet(obj, field, rs);
					}

				}

			}
			
			//Method m = c.getMethod(method, null);
		} catch(Exception e){
			e.printStackTrace();
			filled = false;
		}

		return filled;
	}
	
	private static <T> void setFieldValueFromResultSet(T obj, Field field, ResultSet rs) {
		
		String name = field.getName();
		
		try {
			
			if (rs != null && rs.getObject(name) != null) {
				if (field.getType().isAssignableFrom(Boolean.class)
					|| field.getType().isAssignableFrom(Boolean.TYPE)) {
					
					boolean value = rs.getBoolean(name);
		            field.set(obj, value);
		            
				} else if (field.getType().isAssignableFrom(String.class)) {
					
					String value = rs.getString(name).trim();
		            field.set(obj, value);
		            
				} else if (field.getType().isAssignableFrom(Integer.class)
							|| field.getType().isAssignableFrom(Integer.TYPE)) {
					
					int value = rs.getInt(name);
		            field.set(obj, value);
		            
				} else if (field.getType().isAssignableFrom(Double.class)
							|| field.getType().isAssignableFrom(Double.TYPE)) {
					
					double value = rs.getDouble(name);
		            field.set(obj, value);
		            
				} else if (field.getType().isAssignableFrom(Float.class)
							|| field.getType().isAssignableFrom(Float.TYPE)) {
					
					float value = rs.getFloat(name);
		            field.set(obj, value);
		            
				} else if (field.getType().isAssignableFrom(Long.class)
							|| field.getType().isAssignableFrom(Long.TYPE)) {
					
					long value = rs.getLong(name);
		            field.set(obj, value);
		            
				} else if (field.getType().isAssignableFrom(Date.class)) {
					
					Date value = rs.getDate(name);
		            field.set(obj, value);

				} else if (field.getType().isAssignableFrom(List.class)) {
					String value = rs.getString(name).trim();
					List<String> list = Arrays.asList(value, ",");
		            field.set(obj, list);
				}
			}
			
		} catch (Exception e) {
			String message = e.getMessage();
			if (message.contains("not found") == false || message.contains("Column") == false) {
				e.printStackTrace();
			} else {
				System.out.println("Column: " + name + " not found");
			}
		}
	}
	
	public static Timestamp getCurrentTimestamp() {
		Date today = new java.util.Date();
		return new Timestamp(today.getTime());
	}

}
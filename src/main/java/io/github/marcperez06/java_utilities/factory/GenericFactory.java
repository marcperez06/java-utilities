package io.github.marcperez06.java_utilities.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_utilities.collection.list.ListUtils;
import io.github.marcperez06.java_utilities.reflection.ReflectionUtils;

public class GenericFactory {

	public static <T> T createPageFromClassName(String className, Object...args) {
		T page = null;
		try {
			Class<?> clazz = Class.forName(className);
			page = newInstance(clazz, args);
		} catch (Exception e) {
			page = null;
		}		
		//Assert.assertNotNull("Can not create the page " + className, page);
		return page;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T newInstance(Class<?> clazz, Object...args) {
		T page = null;
		Constructor<?> constructor = null;
		try {
			List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
			
			if (args != null && args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					Class<?> type = ReflectionUtils.getGenericParameterOfClass(args.getClass());
					parameterTypes.add(type);
				}
			}
			
			if (parameterTypes.isEmpty()) {
				constructor = clazz.getConstructor();
			} else {
				Class<?>[] params = ListUtils.toArray(parameterTypes);
				constructor = clazz.getConstructor(params);
			}
			
			page = (T) constructor.newInstance(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

}

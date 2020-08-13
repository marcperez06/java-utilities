 package marc.java_utilities.collection.list;

import java.util.ArrayList;
import java.util.List;

import marc.java_utilities.validation.ValidationUtils;

public class ListUtils {
	
	private ListUtils() {}
	
	public static <T> boolean notExistObjectInList(List<T> list, T value) {
		return !existObjectInList(list, value);
	}
	
	public static <T> boolean existObjectInList(List<T> list, T value) {
		boolean exist = false;
		if (list != null) {
		
			for (int i = 0; i < list.size() && !exist; i++) {
				exist = ValidationUtils.equals(value, list.get(i));
			}
			
		}
		
		return exist;
	}
	
	public static <T> boolean addObjectInList(List<T> list, T value) {
		boolean success = false;
		
		if (list != null && notExistObjectInList(list, value)) {
			
			try {
				list.add(value);
				success = true;	
			} catch (Exception e) {
				success = false;
			}
			
		}

		return success;
	}
	
	public static <T> boolean addObjectInListExceptsEmpty(List<T> list, T value) {
		boolean success = false;
		
		boolean canAdd = (list != null);
		canAdd &= (value != null);
		canAdd &= notExistObjectInList(list, value);
		
		if (value instanceof String) {
			canAdd &= !((String) value).isEmpty();
		}
		
		if (canAdd) {
			success = addObjectInList(list, value);
		}

		return success;
	}
	
	public static <T> boolean removeObjectInList(List<T> list, T value) {
		boolean success = false;
		
		if (list != null && existObjectInList(list, value)) {
			
			try {
				list.remove(value);
				success = true;
			} catch (Exception e) {
				success = false;
			}
			
		}
		
		return success;
	}
	
	public static <T> boolean replaceObjectInList(List<T> list, T value) {
		boolean success = false;
		
		if (list != null) {
			int index = getIndexOfObjectInList(list, value);
			
			try {
				if (index == -1) {
					list.add(value);
					success = true;
				} else {
					list.remove(index);
					list.add(index, value);
					success = true;
				}
			} catch (Exception e) {
				success = false;
			}

		}
		
		return success;
	}
	
	public static <T> int getIndexOfObjectInList(List<T> list, T value) {
		boolean found = false;
		int index = -1;
		boolean areEquals = false;
		
		if (existObjectInList(list, value)) {
			
			for (int i = 0; i < list.size() && !found; i++) {
				
				if (ValidationUtils.equals(value, list.get(i))) {
					index = i;
					found = true;
				}

			}
			
		}
		
		return index;
	}
	
	public static <T> List<Integer> getIndexListOfObjectInList(List<T> list, T value) {
		List<Integer> indexList = new ArrayList<Integer>();
		
		if (existObjectInList(list, value)) {
		
			for (int i = 0; i < list.size(); i++) {
				if (ValidationUtils.equals(value, list.get(i))) {
					indexList.add(i);
				}
			}
			
		}
		
		return indexList;
	}

	public static <T> boolean addObjectInListWithRepeatPossibility(List<T> list, T value) {
		boolean success = false;
		try {
			if (list != null) {
				list.add(value);
				success = true;
			}
		} catch(Exception e) {
			success = false;
		}
		return success;
	}
	
	//TODO: Discutir en Code Review si es realmente util o no
	/* Esta pensada para que llame al equals definido en la clase, es decir, 
	 * podria tener un objeto con id y todo lo demas vacio y en la lista tener el objeto rellenado correctamente.
	 */
	public static <T> T getObjectInList(List<T> list, T value) {
		boolean founded = false;
		T result = null;
		try {
			for (int i = 0; i < list.size() && founded == false; i++) {
				
				boolean areEquals = ValidationUtils.equals(value, list.get(i));
				
				if (areEquals == true) {
					result = list.get(i);
					founded = true;
				}
			}
		} catch(Exception e) {
			result = null;
		}
		return result;
	}

}

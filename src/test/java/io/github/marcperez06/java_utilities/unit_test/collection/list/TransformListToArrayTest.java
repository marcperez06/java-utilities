package io.github.marcperez06.java_utilities.unit_test.collection.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import io.github.marcperez06.java_utilities.collection.list.ListUtils;
import io.github.marcperez06.java_utilities.threads.ThreadUtils;
import io.github.marcperez06.java_utilities.unit_test.threads.task.TaskExample;

public class TransformListToArrayTest {
	
	@Before
	public void beforeTest() {
		System.out.println("----------- Transform List To Array Unit test -----------------");
	}
	
	@Test
	public void transformListToArrayTest() {
		
		List<String> list = new ArrayList<String>();
		list.add("value 1");
		list.add("value 2");
		list.add("value 3");
		
		String[] array = ListUtils.toArray(list);
		
		for (int i = 0; i < list.size(); i++) {
			System.out.println("List Value: " + list.get(i));
			System.out.println("Array Value: " + array[i]);
			assert list.get(i) == array[i];
		}
	}

}

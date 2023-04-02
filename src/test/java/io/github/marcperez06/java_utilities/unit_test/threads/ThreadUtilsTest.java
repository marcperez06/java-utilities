package io.github.marcperez06.java_utilities.unit_test.threads;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import io.github.marcperez06.java_utilities.threads.ThreadUtils;
import io.github.marcperez06.java_utilities.unit_test.threads.task.TaskExample;

public class ThreadUtilsTest {
	
	@Before
	public void beforeTest() {
		System.out.println("----------- Thread Utils Unit test -----------------");
	}
	
	@Test
	public void ThreadPoolTest() {
		int bigArray[] = new int[100];
        for (int i = 0; i < bigArray.length; i++) {
            bigArray[i] = new Random().nextInt(100);
        }
        
        ThreadUtils.createPool(3);
        for (int i = 0; i < 10; i++) {
            Runnable task = new TaskExample(bigArray, i, i + 10);
            ThreadUtils.addTask(task);
        }
        
        ThreadUtils.executeAllTask();
        ThreadUtils.waitUntilExecutionFinish();
        
        System.out.println("Finished all threads");
	}

}

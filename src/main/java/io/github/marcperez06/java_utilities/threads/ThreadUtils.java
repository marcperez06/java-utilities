package io.github.marcperez06.java_utilities.threads;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {

	private static int poolSize = 1;
	private static Queue<Runnable> taskToExecute = new ConcurrentLinkedQueue<Runnable>();
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	
	/**
	 * Create Thread pool of size specified
	 * @param size int - pool size
	 */
	public static void createPool(int size) {
		poolSize = size;
		executor = Executors.newFixedThreadPool(size);
	}
	
	public static void addTask(Runnable task) {
		taskToExecute.add(task);
	}
	
	/**
	 * Execute all task on queue and remove them.
	 */
	public static void executeAllTask() {
		// Execute all task
		for (Runnable task : taskToExecute) {
			executor.execute(task);
			taskToExecute.remove(task);
		}
		
		// Remove task for not execute again
		for (Runnable task : taskToExecute) {
			taskToExecute.remove(task);
		}
	}
	
	/**
	 * Wait until all thread execution ends;
	 * @return boolean - true if finish without issues, false otherwise 
	 */
	public static boolean waitUntilExecutionFinish() {
		boolean finished = false;
		
		try {
			executor.shutdown();
	        while (!executor.isTerminated()) { }
	        finished = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return finished;
	}
	
}
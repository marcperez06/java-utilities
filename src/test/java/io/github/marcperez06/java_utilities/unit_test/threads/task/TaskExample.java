package io.github.marcperez06.java_utilities.unit_test.threads.task;

public class TaskExample implements Runnable {
	 
    private int[] array;
    private int start;
    private int end;
 
    public TaskExample(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
 
    public void run() {
        for (int i = start; i < end; i++) {
            System.out.println(String.format("Current thread %s printing value %s", 
            								Thread.currentThread().getName(), array[i]));
        }
    }
 
}

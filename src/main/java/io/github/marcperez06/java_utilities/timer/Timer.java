/**
 * Simple class to get the time elapsed between startTimer and stopTimer.
 * 
 * @author marcperez06
 */
package io.github.marcperez06.java_utilities.timer;

import java.util.concurrent.TimeUnit;

public class Timer {

	private long startTime;
	private long endTime;
	
	public Timer() {
		this.setStartTime(0);
		this.setEndTime(0);
	}
	
	public long getStartTime() {
		return this.startTime;
	}

	private void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return this.endTime;
	}

	private void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * Return the current Time in milliseconds, same as call System.currentTimeMillis();
	 * @return long - Current time in milliseconds
	 */
	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * Start the timer
	 * @return long - Start time in milliseconds
	 */
	public long startTimer() {
		this.setStartTime(this.getCurrentTime());
		return this.startTime;
	}
	
	/**
	 * Stop the timer
	 * @return long - Time elapsed between start timer and stop timer in milliseconds (endTime - startTime)
	 */
	public long stopTimer() {
		this.setEndTime(this.getCurrentTime());
		return this.getTime();
	}
	
	/**
	 * Return the result of the operation (endTime - startTime) in milliseconds
	 * @return long - Time elapsed between start timer and stop timer in milliseconds 
	 */
	public long getTime() {
		return this.endTime - this.startTime;
	}
	
	/**
	 * Return the result of the operation (endTime - startTime) in TimeUnit specified
	 * @param unit - TimeUnit (seconds, milliseconds, minutes, etc...)
	 * @return long - Time elapsed between start timer and stop timer in TimeUnit specified 
	 */
	public long getTime(TimeUnit unit) {
		long time = this.getTime();
		return transformTimeToUnit(time, unit);
	}
	
	/**
	 * Transform the duration in milliseconds to TimeUnit specified
	 * @param durationInMilliseconds - long Duration in milliseconds
	 * @param unit - TimeUnit (seconds, milliseconds, minutes, etc...)
	 * @return long - Duration in TimeUnit format specified
	 */
	public long transformTimeToUnit(long durationInMilliseconds, TimeUnit unit) {
		TimeUnit sourceUnit = TimeUnit.MILLISECONDS;
		return transformTimeToUnit(durationInMilliseconds, sourceUnit, unit);
	}
	
	/**
	 * Transform the duration with one TimeUnit to another TimeUnit specified
	 * @param duration - long Duration in TimeUnit of sourceUnit
	 * @param sourceUnit - TimeUnit (seconds, milliseconds, minutes, etc...)
	 * @param destinationUnit - TimeUnit (seconds, milliseconds, minutes, etc...)
	 * @return long - Transform the duration with one TimeUnit to another TimeUnit specified
	 */
	public long transformTimeToUnit(long duration, TimeUnit sourceUnit, TimeUnit destinationUnit) {
		return destinationUnit.convert(duration, sourceUnit);
	}

}
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

	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public long startTimer() {
		this.setStartTime(this.getCurrentTime());
		return this.startTime;
	}
	
	public long stopTimer() {
		this.setEndTime(this.getCurrentTime());
		return this.getTime();
	}
	
	public long getTime() {
		return this.endTime - this.startTime;
	}
	
	public long getTime(TimeUnit unit) {
		long time = this.getTime();
		return transformTimeToUnit(time, unit);
	}
	
	public long transformTimeToUnit(long duration, TimeUnit unit) {
		long time = 0;
		
		if (unit == TimeUnit.MILLISECONDS) {
			time = unit.toMillis(duration);
		} else if (unit == TimeUnit.SECONDS) {
			time = unit.toSeconds(duration);
		} else if (unit == TimeUnit.MINUTES) {
			time = unit.toMinutes(duration);
		} else if (unit == TimeUnit.DAYS) {
			time = unit.toDays(duration);
		} else if (unit == TimeUnit.HOURS) {
			time = unit.toHours(duration);
		} else if (unit == TimeUnit.MICROSECONDS) {
			time = unit.toMicros(duration);
		} else if (unit == TimeUnit.NANOSECONDS) {
			time = unit.toNanos(duration);
		}
		
		return time;
	}

}
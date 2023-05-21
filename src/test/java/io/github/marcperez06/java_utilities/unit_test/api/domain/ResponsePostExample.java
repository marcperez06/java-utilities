package io.github.marcperez06.java_utilities.unit_test.api.domain;

public class ResponsePostExample {
	
	private boolean success;
	
	public ResponsePostExample() {
		this.success = false;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	@Override
	public String toString() {
		return "{success : " + this.success + "}";
	}

}
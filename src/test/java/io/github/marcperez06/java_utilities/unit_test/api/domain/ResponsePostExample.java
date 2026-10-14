package io.github.marcperez06.java_utilities.unit_test.api.domain;

public class ResponsePostExample {
	
	private String title;
	private String body;
	private int userId;
	private int id;
	
	public ResponsePostExample() {
		this.title = "";
		this.body = "";
		this.userId = -1;
		this.id = -1;
	}
	
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		String toString = "{title : " + this.title + ",\n";
		toString += "body : " + this.body + ",\n";
		toString += "userId : " + this.userId + ",\n";
		toString += "id : " + this.id + "}";
		return toString;
	}

}
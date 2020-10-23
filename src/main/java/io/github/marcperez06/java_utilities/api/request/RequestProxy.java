package io.github.marcperez06.java_utilities.api.request;

public class RequestProxy {

	private String host;
	private Integer port;
	private String username;
	private String password;
	
	public RequestProxy() {
		this(null, null, null, null);
	}
	
	public RequestProxy(String host, Integer port) {
		this(host, port, null, null);
	}
	
	public RequestProxy(String host, Integer port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAuthenticated() {
		boolean isAuthenticated = (this.username != null && !this.username.isEmpty());
		isAuthenticated &= (this.password != null);
		return isAuthenticated;
	}

}

package io.github.marcperez06.java_utilities.api.request.credentials;

public class RequestCredentials {
	
	private String user;
	private String password;
	private String token;
	
	public RequestCredentials() {
		this.user = null;
		this.password = null;
		this.token = null;
	}
	
	public RequestCredentials(String user, String password) {
		this.user = user;
		this.password = password;
		this.token = null;
	}
	
	public RequestCredentials(String token) {
		this.user = null;
		this.password = null;
		this.token = token;
	}
	
	public String getUser() {
		return this.user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public boolean haveUserCredentials() {
		boolean haveUserCredentials = (this.user != null && !this.user.isEmpty());
		haveUserCredentials &= (this.password != null && !this.password.isEmpty());
		return haveUserCredentials;
	}
	
	public boolean haveTokenCredentials() {
		return (this.token != null && !this.token.isEmpty()); 
	}
	
	public boolean haveCredentials() {
		boolean haveCredentials = this.haveUserCredentials();
		haveCredentials |= this.haveTokenCredentials();
		return haveCredentials;
	}

}
package io.github.marcperez06.java_utilities.database.sql;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import io.github.marcperez06.java_utilities.database.Database;

public class SqlDatabase extends Database {

	private static final String SQL = "jdbc:sqlserver://";
	
	private String database;
	private Map<String, String> connectionSettings;
	
	public SqlDatabase() {
		super();
		this.database = "";
		this.connectionSettings = null;
	}
	
	/**
	 * Constructor of SqlDatabase Object
	 * @param url - String location of database without "jdbc:sqlserver://"
	 * @param database - String database name
	 * @param user - String database user
	 * @param password - String database password
	 */
	public SqlDatabase(String url, String database, String user, String password) {
		this.init(url, database, user, password);
		this.connectionSettings = null;
	}
	
	/**
	 * Constructor of SqlDatabase Object
	 * @param connectionSettings - Map&lt;String, String&gt; connection settings (url, user, password, encrypt, 
	 * 																				trustServerCertificate, loginTimeout, etc)
	 */
	public SqlDatabase(Map<String, String> connectionSettings) {
		if (this.connectionSettings.containsKey("database") && this.connectionSettings.containsKey("url")
				&& this.connectionSettings.containsKey("user") && this.connectionSettings.containsKey("password")) {
			this.init(this.connectionSettings.get("url"), this.connectionSettings.get("database"), 
						this.connectionSettings.get("user"), this.connectionSettings.get("password"));
		} else {
			this.init(null, null, null, null);
		}
	}
	
	private void init(String database, String url, String user, String password) {
		this.database = database;
		super.setUrl(url);
		super.setUser(user);
		super.setPassword(password);
		
		try {
			this.createConnection(url, user, password);
		} catch (Exception e) {
			this.connection = null;
		}
	}
	
	@Override
	public void createConnection(String url, String user, String password)
			throws SQLException, IllegalAccessException, ClassNotFoundException, Exception {

		if (super.connection == null || super.connection.isClosed() == true) {

			try {
				Driver driverDatabase = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
				String stringConnection = this.getConnection(url, user, password);
				super.connection = driverDatabase.connect(stringConnection, new Properties());
			} catch (SQLException e) {
				e.printStackTrace();
				super.connection = null;
			}

		}
	}
	
	private String getConnection(String url, String user, String password) {
		String connection = this.getConnectionUrl(url) + ";";
		connection += "database=" + this.database + ";";
		connection += "user=" + user + ";";
		connection += "password=" + password + ";";
		
		if (this.connectionSettings != null) {
			for (Entry<String, String> entry : this.connectionSettings.entrySet()) {
				if (!this.isRequiredSetting(entry.getKey())) {
					connection += entry.getKey() + "=" + entry.getValue() + ";";
				}
			}
		}
		
		return connection;
	}
	
	private String getConnectionUrl(String url) {
		String connectionUrl = url;
		if (!url.startsWith(SQL)) {
			connectionUrl = SQL + url;
		}
		return connectionUrl;
	}
	
	private boolean isRequiredSetting(String setting) {
		boolean isRequired = (setting.equals("database") || setting.equals("url"));
		isRequired = (setting.equals("user") || setting.equals("password"));
		return isRequired;
	}

}
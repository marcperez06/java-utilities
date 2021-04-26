package io.github.marcperez06.java_utilities.database.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.marcperez06.java_utilities.database.Database;

public class MysqlDatabase extends Database {

	private static final String MYSQL = "jdbc:mysql://";
	
	public MysqlDatabase() {
		super();
	}
	
	/**
	 * Constructor of MysqlDatabase Object
	 * @param url - String location of database without "jdbc:mysql://"
	 * @param user - String database user
	 * @param password - String database password
	 */
	public MysqlDatabase(String url, String user, String password) {
		super(url, user, password);
	}
	
	@Override
	public void createConnection(String url, String user, String password)
			throws SQLException, IllegalAccessException, ClassNotFoundException, Exception {

		if (super.connection == null || super.connection.isClosed() == true) {

			try {
				DriverManager.registerDriver(new com.mysql.jdbc.Driver());
				String connectionUrl = this.getConnectionUrl(url);
				super.connection = DriverManager.getConnection(connectionUrl, user, password);
			} catch (SQLException e) {
				e.printStackTrace();
				super.connection = null;
			}

		}
	}
	
	private String getConnectionUrl(String url) {
		String connectionUrl = url;
		if (!url.startsWith(MYSQL)) {
			connectionUrl = MYSQL + url;
		}
		return connectionUrl;
	}

}

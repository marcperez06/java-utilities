package io.github.marcperez06.java_utilities.database.oracle;

import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.marcperez06.java_utilities.database.Database;
import oracle.jdbc.driver.OracleDriver;

public class OracleDatabase extends Database {
	
	private static final String ORACLE_THIN = "jdbc:oracle:thin:";
	private static final String ORACLE_OCI = "jdbc:oracle:oci:";
	
	private String type;

	public OracleDatabase() {
		super();
		this.setThinType();
	}
	
	/**
	 * Constructor of OracleDatabase Object
	 * @param url - String location of database without "jdbc:oracle:oci:" or "jdbc:oracle:thin:"
	 * @param user - String database user
	 * @param password - String database password
	 */
	public OracleDatabase(String url, String user, String password) {
		super(url, user, password);
		this.setThinType();
	}
	
	/**
	 * Constructor of OracleDatabase Object
	 * @param databaseType - String can be "oci" or "thin"
	 * @param url - String location of database without "jdbc:oracle:oci:" or "jdbc:oracle:thin:"
	 * @param user - String database user
	 * @param password - String database password
	 */
	public OracleDatabase(String databaseType, String url, String user, String password) {
		super(url, user, password);
		this.setType(databaseType);
	}
	
	private void setType(String type) {
		if (type.equalsIgnoreCase("oci")) {
			this.setOciType();
		} else {
			this.setThinType();
		}
	}
	
	public void setThinType() {
		this.type = "thin";
	}
	
	public boolean isThin() {
		return (this.type.equals("thin"));
	}
	
	public void setOciType() {
		this.type = "oci";
	}
	
	public boolean isOci() {
		return (this.type.equals("oci"));
	}

	@Override
	public void createConnection(String url, String user, String password)
			throws SQLException, IllegalAccessException, ClassNotFoundException, Exception {

		if (super.connection == null || super.connection.isClosed() == true) {

			try {
				DriverManager.registerDriver(new OracleDriver());
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
		if (this.isThin()) {
			if (!url.startsWith(ORACLE_THIN)) {
				connectionUrl = ORACLE_THIN + url;	
			}
		} else if (this.isOci()) {
			if (!url.startsWith(ORACLE_OCI)) {
				connectionUrl = ORACLE_OCI + url;	
			}
		}
		return connectionUrl;
	}

}
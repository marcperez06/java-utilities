package io.github.marcperez06.java_utilities.database.factory;

import java.util.Map;

import io.github.marcperez06.java_utilities.database.mysql.MysqlDatabase;
import io.github.marcperez06.java_utilities.database.oracle.OracleDatabase;
import io.github.marcperez06.java_utilities.database.sql.SqlDatabase;

public class DatabaseFactory {

	private DatabaseFactory() { }
	
	/**
	 * Create a MysqlDatabase object
	 * @param url - String
	 * @param user - String
	 * @param password - String
	 * @return MysqlDatabase object
	 */
	public static MysqlDatabase createMysqlDatabase(String url, String user, String password) {
		return new MysqlDatabase(url, user, password);
	}
	
	/**
	 * Create a OracleDatabase of thin type object
	 * @param url - String
	 * @param user - String
	 * @param password - String
	 * @return OracleDatabase object
	 */
	public static OracleDatabase createOracleDatabase(String url, String user, String password) {
		return new OracleDatabase(url, user, password);
	}
	
	/**
	 * Create a OracleDatabase of thin type object
	 * @param url - String
	 * @param user - String
	 * @param password - String
	 * @return OracleDatabase object
	 */
	public static OracleDatabase createOracleDatabaseThin(String url, String user, String password) {
		return new OracleDatabase("thin", url, user, password);
	}
	
	/**
	 * Create a OracleDatabase of oci type object
	 * @param url - String
	 * @param user - String
	 * @param password - String
	 * @return OracleDatabase object
	 */
	public static OracleDatabase createOracleDatabaseOci(String url, String user, String password) {
		return new OracleDatabase("oci", url, user, password);
	}
	
	/**
	 * Create a SqlDatabase object
	 * @param url - String
	 * @param database - String
	 * @param user - String
	 * @param password - String
	 * @return SqlDatabase object
	 */
	public static SqlDatabase createSqlDatabase(String url, String database, String user, String password) {
		return new SqlDatabase(url, database, user, password);
	}
	
	/**
	 * Create a SqlDatabase object
	 * @param connectionSettings - Map&lt;String, String&gt; connection params like url, database, user, password, etc...
	 * 								for more information visit: https://docs.microsoft.com/es-es/sql/connect/jdbc/step-3-proof-of-concept-connecting-to-sql-using-java?view=sql-server-ver15
	 * @return SqlDatabase object
	 */
	public static SqlDatabase createSqlDatabase(Map<String, String> connectionSettings) {
		return new SqlDatabase(connectionSettings);
	}

}

/**
 * Classe base para acceso a base de datos.
 * @author Marc Perez Rodriguez
 */

package io.github.marcperez06.java_utilities.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public abstract class Database {
	
	private String url;
	private String user;
	private String password;

	protected Connection connection;
	protected Statement statement;
	protected PreparedStatement preparedStatement;
	protected ResultSet resultSet;
	
	public Database() {
		this.url = null;
		this.user = null;
		this.password = null;
		this.connection = null;
		this.statement = null;
		this.preparedStatement = null;
		this.resultSet = null;
	}
	
	public Database(String url, String user, String password) {
		this();
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	protected void finalize() {
		this.close();
	}
	
	/**
	 * @return url - string
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * @param url string - url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return user - string
	 */
	public String getUser() {
		return this.user;
	}
	
	/**
	 * @param user string - database user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * @return password - string
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * @param password string - database password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public abstract void createConnection(String url, String user, String password) 
									throws SQLException, IllegalAccessException, ClassNotFoundException, Exception;
	
	/**
	 * @return connection - Connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * @param connection Connection - connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * @return Statement - statement
	 */
	public Statement getStatement() {
		return this.statement;
	}
	
	/**
	 * @return PreparedStatement - prepared statement
	 */
	public PreparedStatement getPreparedStatement() {
		return this.preparedStatement;
	}
	
	/**
	 * @return ResultSet - result set
	 */
	public ResultSet getResultSet() {
		ResultSet resultSet = null;
		try {
			if (this.resultSet != null && this.resultSet.isClosed()) {
				resultSet = this.resultSet;
			}
		} catch (SQLException e) {
			resultSet = null;
		}
		return resultSet;
	}
	
	private void closeResource(AutoCloseable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (resource != null) {
					try {
						resource.close();	
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	/**
	 * Execute sql in database
	 * @param sql String - sql to execute
	 * @return rows affected - int
	 */
	public int executeSQL(String sql) {
		int success = 0;
		try {
			
			this.openConnection();
			this.statement = this.createStatement();
			
			if (this.statement != null) {
				success = this.statement.executeUpdate(sql);
				this.closeResource(this.statement);
				this.closeResource(this.connection);
			}

		} catch (Exception e) {
			e.printStackTrace();
			success = 0;
		}
		return success;
	}
	
	/**
	 * Opens the connection with database
	 */
	public void openConnection() {
		try {
			if (this.connection == null || this.connection.isClosed()) {
				this.createConnection(this.url, this.user, this.password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a Statement
	 * @return statement - Statement
	 */
	public Statement createStatement() {
		Statement st = null;
		try {
			if (this.connection != null && !this.connection.isClosed()) {
				this.closeResource(this.statement);
				
				st = this.connection.createStatement();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			st = null;
		}
		return st;
	}
	
	/**
	 * Execute a query to database
	 * @param sql String - sql to execute
	 */
	public void executeQuery(String sql) {
		try {

			this.openConnection();
			this.statement = this.createStatement();
			
			if (this.statement != null) {
				this.resultSet = this.statement.executeQuery(sql);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Execute prepared sql to database
	 * @param sql String - prepared sql to execute
	 * @param parameters List&lt;Object&gt; - parameters to fill prepared sql
	 * @return rows affected - int
	 */
	public int executePreparedSQL(String sql, List<Object> parameters) {
		int success = 0;
		try {

			this.openConnection();
			
			if (this.connection != null) {
				this.preparedStatement = this.createPreparedStatement(sql);
				
				for (int i = 0; i < parameters.size(); i++) {
					this.setPreparedValue(i, parameters.get(i));
				}
				
				if (this.preparedStatement != null) {
					success = this.preparedStatement.executeUpdate();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			success = 0;
		}
		return success;
	}
	
	/**
	 * Creates a prepared Statement with the sql specified
	 * @param sql String - sql to execute
	 * @return preparedStatement - PreparedStatement
	 */
	private PreparedStatement createPreparedStatement(String sql) {
		PreparedStatement st = null;
		try {
			if (this.connection != null && this.connection.isClosed()) {
				this.closeResource(this.preparedStatement);
				st = this.connection.prepareStatement(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			st = null;
		}
		return st;
	}
	
	/**
	 * Execute prepared query to database
	 * @param sql String - prepared sql to execute
	 * @param parameters List&lt;Object&gt; - parameters to fill prepared sql
	 */
	public void executePreparedQuery(String sql, List<Object> parameters) {
		try {
			
			this.openConnection();
			
			if (this.connection != null) {
				this.preparedStatement = this.createPreparedStatement(sql);
				
				for (int i = 0; i < parameters.size(); i++) {
					this.setPreparedValue(i, parameters.get(i));
				}
				
				if (this.preparedStatement != null) {
					this.resultSet = this.preparedStatement.executeQuery();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void setPreparedValue(int indexOfParameter, Object parameter) throws SQLException {
		
		// correct the index of parameter value, to avoid exception, prepared statement values start in 1.
		int index = indexOfParameter + 1;
		
		if (this.preparedStatement != null) {
		
			if (parameter == null) {
				
				this.preparedStatement.setObject(index, parameter);
			
			} else if (parameter instanceof String) {
				
				String value = (String) parameter;
				this.preparedStatement.setString(index, value);
			
			} else if (parameter instanceof Integer) {
				
				Integer value = (Integer) parameter;
				this.preparedStatement.setInt(index, value.intValue());
			
			} else if (parameter instanceof Double) {
				
				Double value = (Double) parameter;
				this.preparedStatement.setDouble(index, value.doubleValue());
			
			} else if (parameter instanceof Float) {
				
				Float value = (Float) parameter;
				this.preparedStatement.setFloat(index, value.floatValue());
			
			} else if (parameter instanceof Long) {
				
				Long value = (Long) parameter;
				this.preparedStatement.setLong(index, value.longValue());
			
			} else if (parameter instanceof Date) {
				
				Date value = (Date) parameter;
				this.preparedStatement.setDate(index, value);
			
			} else if (parameter instanceof Timestamp) {
				
				Timestamp value = (Timestamp) parameter;
				this.preparedStatement.setTimestamp(index, value);

			} else if (parameter instanceof java.util.Date) {
				
				java.util.Date javaDate = (java.util.Date) parameter;
				if (javaDate != null) {
					Date value = new Date(javaDate.getTime());
					this.preparedStatement.setDate(index, value);
				}
				
			}

		}
	}
	
	/**
	 * Close statement, prepared statement, result set and connection
	 */
	public void close() {
		this.closeResource(this.statement);
		this.closeResource(this.preparedStatement);
		this.closeResource(this.resultSet);
		this.closeResource(this.connection);
	}

}

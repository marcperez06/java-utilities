/**
 * Classe base para acceso a base de datos.
 * @author Marc Perez Rodriguez
 */

package io.github.marcperez06.java_utilities.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class DB {
	
	private String url;
	private String user;
	private String password;

	private Connection connection;
	private Statement statement;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	
	public DB() {
		this.connection = null;
		this.statement = null;
		this.preparedStatement = null;
		this.resultSet = null;
	}
	
	public DB(String url, String user, String password) {
		try {
			this.url = url;
			this.user = user;
			this.password = password;
			this.createConnection(url, user, password);
		} catch (Exception e) {
			this.connection = null;
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() {
		this.close();
	}

	public void createConnection(String url, String user, String password) 
									throws SQLException, IllegalAccessException, ClassNotFoundException, Exception {
		
		if (this.connection == null || this.connection.isClosed() == true) {
			
			try {
				DriverManager.registerDriver(new com.mysql.jdbc.Driver());
				this.connection = DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				e.printStackTrace();
				this.connection = null;
			}
		
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public Statement getStatement() {
		return this.statement;
	}
	
	public PreparedStatement getPreparedStatement() {
		return this.preparedStatement;
	}
	
	public ResultSet getResultSet() {
		ResultSet resultSet = null;
		try {
			if (this.resultSet != null && this.resultSet.isClosed() == false) {
				resultSet = this.resultSet;
			}
		} catch (SQLException e) {
			resultSet = null;
		}
		return resultSet;
	}

	public int executeSQL(String sql) {
		int success = 0;
		try {
			
			this.openConnection();
			this.statement = this.createStatement();
			
			if (this.statement != null) {
				success = this.statement.executeUpdate(sql);
				this.statement.close();
				this.connection.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			success = 0;
		}
		return success;
	}
	
	public void openConnection() {
		try {
			if (this.connection != null && this.connection.isClosed() == true) {
				this.createConnection(this.url, this.user, this.password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Statement createStatement() {
		Statement st = null;
		try {
			if (this.connection != null) {
				
				if (this.statement != null) {
					this.statement.close();
				}
				
				st = this.connection.createStatement();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			st = null;
		}
		return st;
	}
	
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
	
	public PreparedStatement createPreparedStatement(String sql) {
		PreparedStatement st = null;
		try {
			if (this.connection != null) {
				
				if (this.preparedStatement != null) {
					this.preparedStatement.close();
				}
				
				st = this.connection.prepareStatement(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			st = null;
		}
		return st;
	}
	
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
	
	public void close() {
		try {
			
			if (this.statement != null) {
				this.statement.close();
			}
			
			if (this.preparedStatement != null) {
				this.preparedStatement.close();
			}
			
			if (this.connection != null) {
				this.connection.close();
			}
			
			if (this.resultSet != null) {
				this.resultSet.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

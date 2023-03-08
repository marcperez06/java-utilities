package io.github.marcperez06.java_utilities.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_utilities.database.interfaces.IDAO;
import io.github.marcperez06.java_utilities.database.utils.DatabaseUtils;

public abstract class BaseDAO implements IDAO {

	protected String url;
	protected String user;
	protected String password;
	
	protected Database db;
	
	public BaseDAO() {
		this.db = null;
	}
	
	public BaseDAO(String url, String user, String password) {
		this.createDatabaseConnection(url, user, password);
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setCredentials(String user, String password) {
		this.user = user;
		this.password = password;
	}
	
	protected boolean haveAccessToDB() {
		return (this.db != null);
	}
	
	/**
	 * Create a connection to database using the url, user and password parameters
	 * @param url - String
	 * @param user - String
	 * @param password - String
	 */
	@Override
	public void createDatabaseConnection(String url, String user, String password) {
		this.setUrl(url);
		this.setCredentials(user, password);
		this.createDatabaseConnection();
	}
	
	/**
	 * Create a connection to database using the url, user and password properties.
	 */
	@Override
	public void createDatabaseConnection() {
		try {
			this.db = this.createDatabaseObject();
			
			if (this.db.getConnection() == null) {
				this.db = null;
			}
			
		} catch (Exception e) {
			this.db = null;
		}
	}
	
	protected abstract Database createDatabaseObject();

	@Override
	public int insert(SqlObject sqlObject) {
		int result = -1;
		if (this.haveAccessToDB() == true) {
			result = this.sqlInsert(sqlObject);
		}
		return result;
	}
	
	protected abstract int sqlInsert(SqlObject sqlObject);
	
	@Override
	public int update(SqlObject sqlObject) {
		int result = -1;
		if (this.haveAccessToDB() == true) {
			result = this.sqlUpdate(sqlObject);
		}
		return result;
	}
	
	protected abstract int sqlUpdate(SqlObject sqlObject);
	
	private <T> Method getWhereConditionMethodOfPK(T pk) {
		Method method = null;
		if (pk != null) {
			try {
				Class<?> clazz = pk.getClass();
				method = clazz.getDeclaredMethod("getWhereCondition");
			} catch (NoSuchMethodException e) {
				method = null;
				e.printStackTrace();
			} catch (SecurityException e) {
				method = null;
				e.printStackTrace();
			}
		}
		
		return method;
	}
	
	protected <T> boolean deleteUsingPK(T pk) {
		boolean delete = false;
		
		try {

			Method method = this.getWhereConditionMethodOfPK(pk);
			
			if (method != null) {
				
				SqlObject sqlObject = (SqlObject) method.invoke(pk);
	
				int result = this.delete(sqlObject);
				delete = (result >= 1) ? true : false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return delete;
	}
	
	@Override
	public int delete(SqlObject sqlObject) {
		int result = -1;
		if (this.haveAccessToDB()) {
			result = this.sqlDelete(sqlObject);
		}
		return result;
	}
	
	protected abstract int sqlDelete(SqlObject sqlObject);
	
	protected <T> List<T> returnListOfData(SqlObject sqlObject, Class<T> returnClass) {
		List<T> data = new ArrayList<T>();
		
		if (this.haveAccessToDB()) {
			//this.db.openConnection();
			
			this.select(sqlObject);
			ResultSet resultSet = this.db.getResultSet();
			data = DatabaseUtils.getListFromResultSet(resultSet, returnClass);
			//this.db.close();
		}
		
		return data;
	}
	
	protected <T, P> T selectUsingPK(Class<T> returnClass, P pk) {
		
		T classToReturn = null;

		try {
		
			Constructor<T> constructor = returnClass.getConstructor();
			classToReturn = constructor.newInstance();
			Method method = this.getWhereConditionMethodOfPK(pk);
			
			if (method != null && this.existUsingPK(pk) == true) {
				
				SqlObject sqlObject = (SqlObject) method.invoke(pk);
				this.select(sqlObject);
				
				ResultSet rs = this.db.getResultSet();
				DatabaseUtils.fillFromResultSet(rs, classToReturn);
				//this.db.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return classToReturn;
	}
	
	@Override
	public void select(SqlObject sqlObject) {
		if (this.haveAccessToDB()) {
			this.sqlSelect(sqlObject);
		}
	}
	
	protected abstract void sqlSelect(SqlObject sqlObject);
	
	protected <T> boolean existUsingPK(T pk) {
		boolean exist = false;

		try {
			
			Method method = this.getWhereConditionMethodOfPK(pk);
			
			if (method != null) {
			
				SqlObject sqlObject = (SqlObject) method.invoke(pk);
				exist = this.exist(sqlObject);
	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return exist;
	}
	
	@Override
	public boolean exist(SqlObject sqlObject) {
		boolean result = false;
		if (this.haveAccessToDB()) {
			result = this.sqlExist(sqlObject);
		}
		return result;
	}
	
	protected abstract boolean sqlExist(SqlObject sqlObject);
	
	protected boolean getExistResult(String sql, SqlObject sqlObject) {
		boolean exist = false;
		
		try {
			
			this.db.executePreparedQuery(sql, sqlObject.getWhereParameters());
			ResultSet resultSet = this.db.getResultSet();
			
			while(resultSet.next()) {
				int result = resultSet.getInt("exist");
				exist = (result >= 1) ? true : false;
			}
			
			//this.db.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			exist = false;
		}
		
		return exist;
	}
	
	protected <T> int numRegistersUsingPK(T pk) {
		int numRegisters = 0;

		try {
			
			Method method = this.getWhereConditionMethodOfPK(pk);
			
			if (method != null) {
			
				SqlObject sqlObject = (SqlObject) method.invoke(pk);
				numRegisters = this.numRegisters(sqlObject);
	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return numRegisters;
	}
	
	@Override
	public int numRegisters(SqlObject sqlObject) {
		int numRegisters = 0;
		if (this.haveAccessToDB() == true) {
			numRegisters = this.sqlNumRegisters(sqlObject);
		}
		return numRegisters;
	}
	
	protected abstract int sqlNumRegisters(SqlObject sqlObject);
	
	protected int getNumRegisters(String sql, SqlObject sqlObject) {
		int numRegisters = 0;
		
		try {
			
			this.db.executePreparedQuery(sql, sqlObject.getWhereParameters());
			ResultSet resultSet = this.db.getResultSet();
			
			while(resultSet.next()) {
				numRegisters = resultSet.getInt("numRegisters");
			}
			
			//this.db.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			numRegisters = 0;
		}
		
		return numRegisters;
	}

}
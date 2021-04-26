/**
 * Classe base para todos los DAO.
 * @author Marc Perez Rodriguez
 */

package io.github.marcperez06.java_utilities.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_utilities.strings.StringUtils;

public class DAO {
	
	private String url;
	private String user;
	private String password;
	
	protected Database db;

	public DAO() {
		this.db = null;
	}
	
	public DAO(String url, String user, String password) {
		this.createDatabaseConnection(url, user, password);
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setCredentials(String user, String password) {
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Create a connection to database using the url, user and password parameters
	 * @param url - String
	 * @param user - String
	 * @param password - String
	 */
	public void createDatabaseConnection(String url, String user, String password) {
		this.setUrl(url);
		this.setCredentials(user, password);
		this.createDatabaseConnection();
	}
	
	/**
	 * Create a connection to database using the url, user and password properties.
	 */
	public void createDatabaseConnection() {
		try {
			this.db = new Database(this.url, this.user, this.password);
			
			if (this.db.getConnection() == null) {
				this.db = null;
			}
			
		} catch (Exception e) {
			this.db = null;
		}
	}
	
	protected boolean haveAccessToDB() {
		return (this.db != null);
	}
	
	protected int insert(SqlObject sqlObject) {
		int result = -1;
		if (this.haveAccessToDB() == true) {
			result = this.insertInDB(sqlObject);
		}
		return result;
	}
	
	private int insertInDB(SqlObject sqlObject) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO " + sqlObject.getTableName());
		sqlBuilder.append("(" + StringUtils.concatArrayOfString(sqlObject.getFields(), ", ") + ") VALUES");
		sqlBuilder.append("(");

		for (int i = 0; i < sqlObject.getFieldsSize(); i++) {
			sqlBuilder.append("?, ");
		}
		
		String sql = sqlBuilder.toString();
		sql = StringUtils.cutStringWithOtherString(sql, ", ", 0);
		sql += ");";

		int insert = this.db.executePreparedSQL(sql, sqlObject.getParameters());
		
		System.out.println("------------ DAO.java ------------ SQL INSERT: " + sql);
		
		return insert;
	}
	
	protected int update(SqlObject sqlObject) {
		int result = -1;
		if (this.haveAccessToDB() == true) {
			result = this.updateInDB(sqlObject);
		}
		return result;
	}
	
	private int updateInDB(SqlObject sqlObject) {
		String sql = "UPDATE " + sqlObject.getTableName();
		sql += " SET ";

		for (int i = 0; i < sqlObject.getFieldsSize(); i++) {
			sql += sqlObject.getField(i) + " = ?, ";
		}

		sql = StringUtils.cutStringWithOtherString(sql, ", ", 0);
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ DAO.java ------------ SQL UPDATE: " + sql);

		int update = this.db.executePreparedSQL(sql, sqlObject.getJoinedParameters());
		
		return update;
	}
	
	private <T> Method getWhereConditionMethodOfPK(T pk) {
		Method method = null;
		if (pk != null) {
			try {
				Class clazz = pk.getClass();
				method = clazz.getMethod("getWhereCondition");
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
	
	protected int delete(SqlObject sqlObject) {
		int result = -1;
		if (this.haveAccessToDB() == true) {
			result = this.deleteInDB(sqlObject);
		}
		return result;
	}
	
	private int deleteInDB(SqlObject sqlObject) {
		String sql = "DELETE " + sqlObject.getTableName();
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ DAO.java ------------ SQL DELETE: " + sql);

		int delete = this.db.executePreparedSQL(sql, sqlObject.getWhereParameters());
		
		return delete;
	}
	
	protected <T> List<T> returnListOfData(SqlObject sqlObject, Class<T> returnClass) {
		List<T> data = new ArrayList<T>();
		
		if (this.haveAccessToDB() == true) {
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
	
	protected void select(SqlObject sqlObject) {
		if (this.haveAccessToDB() == true) {
			this.selectInDB(sqlObject);
		}
	}
	
	private void selectInDB(SqlObject sqlObject) {
		
		String auxFields = "*";
		if (sqlObject.haveFields() == true) {
			auxFields = sqlObject.getConcatFields();
		}

		String sql = "SELECT " + auxFields;
		sql += " FROM " + sqlObject.getTableName();
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ DAO.java ------------ SQL SELECT: " + sql);

		this.db.executePreparedQuery(sql, sqlObject.getWhereParameters());
	}
	
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
	
	protected boolean exist(SqlObject sqlObject) {
		boolean result = false;
		if (this.haveAccessToDB() == true) {
			result = this.existInDB(sqlObject);
		}
		return result;
	}
	
	private boolean existInDB(SqlObject sqlObject) {
		String sql = "SELECT COUNT(*) as exist";
		sql += " FROM " + sqlObject.getTableName();
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ DAO.java ------------ SQL EXIST: " + sql);

		boolean exist = this.getExistResult(sql, sqlObject);
		
		return exist;
	}
	
	private boolean getExistResult(String sql, SqlObject sqlObject) {
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
	
	protected int numRegisters(SqlObject sqlObject) {
		int numRegisters = 0;
		if (this.haveAccessToDB() == true) {
			numRegisters = this.numRegistersInDB(sqlObject);
		}
		return numRegisters;
	}
	
	protected int numRegistersInDB(SqlObject sqlObject) {
		int numRegisters = 0;
		String sql = "SELECT COUNT(*) as numRegisters";
		sql += " FROM " + sqlObject.getTableName();
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ DAO.java ------------ SQL COUNT: " + sql);
		
		numRegisters = this.getNumRegisters(sql, sqlObject);
		
		return numRegisters;
	}
	
	private int getNumRegisters(String sql, SqlObject sqlObject) {
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

/**
 * Classe base para todos los DAO.
 * @author Marc Perez Rodriguez
 */

package io.github.marcperez06.java_utilities.database.mysql;

import io.github.marcperez06.java_utilities.database.BaseDAO;
import io.github.marcperez06.java_utilities.database.Database;
import io.github.marcperez06.java_utilities.database.SqlObject;
import io.github.marcperez06.java_utilities.database.factory.DatabaseFactory;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public class MysqlDAO extends BaseDAO {

	public MysqlDAO() {
		super();
	}
	
	public MysqlDAO(String url, String user, String password) {
		super(url, user, password);
	}
	
	@Override
	protected Database createDatabaseObject() {
		return DatabaseFactory.createMysqlDatabase(super.url, super.user, super.password);
	}
	
	@Override
	protected int insertInDB(SqlObject sqlObject) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO " + sqlObject.getTableName());
		sqlBuilder.append("(" + sqlObject.getConcatFields() + ") VALUES");
		sqlBuilder.append("(");

		for (int i = 0; i < sqlObject.getFieldsSize(); i++) {
			sqlBuilder.append("?, ");
		}
		
		String sql = sqlBuilder.toString();
		sql = StringUtils.cutStringWithOtherString(sql, ", ", 0);
		sql += ");";

		int insert = super.db.executePreparedSQL(sql, sqlObject.getParameters());
		
		System.out.println("------------ MysqlDAO.java ------------ SQL INSERT: " + sql);
		
		return insert;
	}
	
	@Override
	protected int updateInDB(SqlObject sqlObject) {
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
		
		System.out.println("------------ MysqlDAO.java ------------ SQL UPDATE: " + sql);

		int update = super.db.executePreparedSQL(sql, sqlObject.getJoinedParameters());
		
		return update;
	}
	
	@Override
	protected int deleteInDB(SqlObject sqlObject) {
		String sql = "DELETE " + sqlObject.getTableName();
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ MysqlDAO.java ------------ SQL DELETE: " + sql);

		int delete = super.db.executePreparedSQL(sql, sqlObject.getWhereParameters());
		
		return delete;
	}
	
	@Override
	protected void selectInDB(SqlObject sqlObject) {
		
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
		
		System.out.println("------------ MysqlDAO.java ------------ SQL SELECT: " + sql);

		super.db.executePreparedQuery(sql, sqlObject.getWhereParameters());
	}
	
	@Override
	protected boolean existInDB(SqlObject sqlObject) {
		String sql = "SELECT COUNT(*) as exist";
		sql += " FROM " + sqlObject.getTableName();
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ MysqlDAO.java ------------ SQL EXIST: " + sql);

		boolean exist = this.getExistResult(sql, sqlObject);
		
		return exist;
	}
	
	@Override
	protected int numRegistersInDB(SqlObject sqlObject) {
		int numRegisters = 0;
		String sql = "SELECT COUNT(*) as numRegisters";
		sql += " FROM " + sqlObject.getTableName();
		sql += " WHERE 1 = 1";

		for (int i = 0; i < sqlObject.getWhereFieldsSize(); i++) {
			sql += " AND " + sqlObject.getWhereField(i) + " = ?";
		}
		
		sql += ";";
		
		System.out.println("------------ MysqlDAO.java ------------ SQL COUNT: " + sql);
		
		numRegisters = super.getNumRegisters(sql, sqlObject);
		
		return numRegisters;
	}

}
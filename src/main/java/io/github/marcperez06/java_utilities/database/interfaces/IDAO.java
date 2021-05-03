package io.github.marcperez06.java_utilities.database.interfaces;

import io.github.marcperez06.java_utilities.database.SqlObject;

public interface IDAO {

	public void createDatabaseConnection();
	public void createDatabaseConnection(String url, String user, String password);
	public int insert(SqlObject sqlObject);
	public int update(SqlObject sqlObject);
	public int delete(SqlObject sqlObject);
	public void select(SqlObject sqlObject);
	public boolean exist(SqlObject sqlObject);
	public int numRegisters(SqlObject sqlObject);

}
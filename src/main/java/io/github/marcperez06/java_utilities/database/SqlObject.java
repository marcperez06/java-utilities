/**
 * Classe encargada de contener todos los parametros necesarios para ejecutar las SQL.
 * @author Marc Perez Rodriguez
 */

package io.github.marcperez06.java_utilities.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_utilities.strings.StringUtils;

public class SqlObject {
	
	private String tableName;
	private String[] fields;
	private List<Object> parameters;
	private String[] whereFields;
	private List<Object> whereParameters;
	
	public SqlObject() {
		this.tableName = "";
		this.fields = null;
		this.parameters = new ArrayList<Object>();
		this.whereFields = null;
		this.whereParameters = new ArrayList<Object>();
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String[] getFields() {
		return this.fields;
	}
	
	public void setFields(String[] fields) {
		this.fields = fields;
	}
	
	public String getConcatFields() {
		return StringUtils.concatArrayOfString(this.fields, ",");
	}
	
	public List<Object> getParameters() {
		return this.parameters;
	}
	
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
	
	public String[] getWhereFields() {
		return this.whereFields;
	}
	
	public void setWhereFields(String[] whereFields) {
		this.whereFields = whereFields;
	}
	
	public List<Object> getWhereParameters() {
		return this.whereParameters;
	}
	
	public void setWhereParameters(List<Object> whereParameters) {
		this.whereParameters = whereParameters;
	}
	
	public int getFieldsSize() {
		return (this.fields != null) ? this.fields.length : 0;
	}
	
	public boolean haveFields() {
		return (this.getFieldsSize() > 0);
	}
	
	public int getWhereFieldsSize() {
		return (this.whereFields != null) ? this.whereFields.length : 0;
	}
	
	public boolean haveWhereFields() {
		return (this.getWhereFieldsSize() > 0);
	}
	
	public int getParametersSize() {
		return this.parameters.size();
	}
	
	public int getWhereParametersSize() {
		return this.whereParameters.size();
	}
	
	public void addParameter(Object parameter) {
		this.parameters.add(parameter);
	}

	public void addWhereParameter(Object whereParameter) {
		this.whereParameters.add(whereParameter);
	}
	
	public List<Object> getJoinedParameters() {
		List<Object> joinedParameters = new ArrayList<Object>();
		joinedParameters.addAll(this.parameters);
		joinedParameters.addAll(this.whereParameters);
		return joinedParameters;
	}
	
	public String getField(int index) {
		String field = "";
		if (index > -1 && index < this.getFieldsSize()) {
			field = this.fields[index];
		}
		return field;
	}
	
	public String getWhereField(int index) {
		String whereField = "";
		if (index > -1 && index < this.getWhereFieldsSize()) {
			whereField = this.whereFields[index];
		}
		return whereField;
	}
	
	public Object getParameter(int index) {
		Object parameter = null;
		if (index > -1 && index < this.getParametersSize()) {
			parameter = this.parameters.get(index);
		}
		return parameter;
	}
	
	public Object getWhereParameter(int index) {
		Object whereParameter = null;
		if (index > -1 && index < this.getWhereParametersSize()) {
			whereParameter = this.whereParameters.get(index);
		}
		return whereParameter;
	}

	public <T> void addAllParameter(T obj) {
		try {
			
			Class clazz = Class.forName(obj.getClass().getName());
			List<Field> fields = this.getFieldsOfDBInClass(clazz);
			
			for (Field field : fields) {
				field.setAccessible(true);
				this.addParameter(field.get(obj));
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private List<Field> getFieldsOfDBInClass(Class c) {
		ArrayList<Field> fields = new ArrayList<Field>();
		
		try {
			
			Class parentClass = c.getSuperclass();
			
			while (parentClass != null && parentClass.getName().equals("java.lang.Object") == false) {
				Field[] parentFields = parentClass.getDeclaredFields();
				fields.addAll(this.returnExistingFields(parentFields));
				parentClass = parentClass.getSuperclass();
			}
			
			Field[] classFields = c.getDeclaredFields();
			fields.addAll(this.returnExistingFields(classFields));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
	private ArrayList<Field> returnExistingFields(Field[] classFields) {
		ArrayList<Field> existingFields = new ArrayList<Field>();
		
		for (Field field : classFields) {
			
			boolean exist = false;
			
			for (int i = 0; i < this.fields.length && exist == false; i++) {
				
				String fieldName = field.getName();
				String fieldInDB = this.fields[i];
				
				if (fieldName.equals(fieldInDB) == true) {
					existingFields.add(field);
					exist = true;
				}
			}
			
			
		}
		
		return existingFields;
	}

}
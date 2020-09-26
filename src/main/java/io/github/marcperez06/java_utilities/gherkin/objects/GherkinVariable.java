package io.github.marcperez06.java_utilities.gherkin.objects;

public class GherkinVariable {

	private String variableName;
	private Object variableValue;
	private Class<?> variableType;
	
	public GherkinVariable(String variableName, Object variableValue) {
		this.variableName = variableName;
		this.variableValue = variableValue;
		this.calculateVariableType();
	}
	
	private void calculateVariableType() {
		if (this.variableValue instanceof String) {
			
			String auxValue = (String) this.variableValue;
			
			boolean isInt = auxValue.matches("-?\\d+");
			boolean isFloat = auxValue.matches("-?\\d+\\.\\d+");
			
			if (isInt) {
				this.variableType = Integer.TYPE;
			} else if (isFloat) {
				this.variableType = Float.TYPE;
			} else {
				this.variableType = String.class;
			}
			
		} else {
			this.variableType = variableValue.getClass();
		}
	}

	public String getVariableName() {
		return this.variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public Object getVariableValue() {
		return this.variableValue;
	}

	public void setVariableValue(Object variableValue) {
		this.variableValue = variableValue;
	}

	public Class<?> getVariableType() {
		return this.variableType;
	}

	public void setVariableType(Class<?> variableType) {
		this.variableType = variableType;
	}
	
}

package io.github.marcperez06.java_utilities.numbers;

import java.text.NumberFormat;
import java.util.Locale;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class NumberUtils {

	private NumberUtils() { }
	
	/**
	 * Transform European number (example: 8,5) to American number (example: 8.5)
	 * @param number - String
	 * @return String - number transformed to American format
	 */
	public static String transformEuropeanToAmericanFormat(String number) {
		String americanNumber = "";
		
		if (number != null && !number.isEmpty()) {
			String[] numberParts = number.split("\\,");
			if (numberParts != null && numberParts.length > 1) {
				americanNumber = numberParts[0].replaceAll("\\.", "") + "." + numberParts[1];	
			} else {
				americanNumber = numberParts[0];
			}
		}
		
		return americanNumber;
	}
	
	/**
	 * Transform American number (example: 8,5) to European number (example: 8.5)
	 * @param number - String
	 * @return String - number transformed to European format
	 */
	public static String transformAmericanToEuropeanFormat(String number) {
		String europeanNumber = "";
		
		if (number != null && !number.isEmpty()) {
			String[] numberParts = number.split("\\.");
			if (numberParts != null && numberParts.length > 1) {
				europeanNumber = numberParts[0].replaceAll("\\,", "") + "," + numberParts[1];	
			} else {
				europeanNumber = numberParts[0];
			}
		}
		
		return europeanNumber;
	}
	
	/**
	 * Evaluate mathematical expression in string format using javascript engine
	 * @param expression - String
	 * @return double - Result of mathematical expression 
	 */
	public static double evaluateMathExpression(String expression) {
		double result = 0;
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("js");
		
		expression = replaceArithmeticFormula(expression);

		try {
			Object jsResult = scriptEngine.eval(expression);
			
			if (jsResult != null) {
				result = Double.valueOf(jsResult.toString());
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static String replaceArithmeticFormula(String expression) {
		expression = expression.replaceAll("sin", "Math.sin")
				                .replaceAll("cos", "Math.cos")
				                .replaceAll("tan", "Math.tan")
				                .replaceAll("sqrt", "Math.sqrt")
				                .replaceAll("log", "Math.log")
				                .replaceAll("log10", "Math.log10")
				                .replaceAll("pi", "Math.PI")
								.replaceAll("round", "Math.round")
						        .replaceAll("ceil", "Math.ceil")
								.replaceAll("floor", "Math.floor")
				                .replaceAll("abs", "Math.abs")
								.replaceAll("random", "Math.random")
								.replaceAll("max", "Math.max")
								.replaceAll("min", "Math.min")
				                .replaceAll("pow", "Math.pow")
								.replaceAll("exp", "Math.exp");
		
		return expression;
	}
	
	/**
	 * Format number to european format with 2 decimals
	 * @param number - double
	 * @return String - number formatted
	 */
	public static String toEuropeanFormat(double number) {
		return toEuropeanFormat(number, 2);
	}

	/**
	 * Format number to european format with decimals specified
	 * @param number - double
	 * @param decimals - int decimals wished
	 * @return String - number formatted
	 */
	public static String toEuropeanFormat(double number, int decimals) {
		NumberFormat formatter = NumberFormat.getInstance(Locale.ITALIAN);
		formatter.setMinimumFractionDigits(decimals);
		formatter.setMaximumFractionDigits(decimals);
		String numberFormatted = formatter.format(number);
		return numberFormatted;
	}
	
	/**
	 * Format number to american format with 2 decimals
	 * @param number - double
	 * @return String - number formatted
	 */
	public static String toAmericanFormat(double number) {
		return toAmericanFormat(number, 2);
	}

	/**
	 * Format number to american format with decimals specified
	 * @param number - double
	 * @param decimals - int decimals wished
	 * @return String - number formatted
	 */
	public static String toAmericanFormat(double number, int decimals) {
		NumberFormat formatter = NumberFormat.getInstance(Locale.US);
		formatter.setMinimumFractionDigits(decimals);
		formatter.setMaximumFractionDigits(decimals);
		String numberFormatted = formatter.format(number);
		return numberFormatted;
	}

}
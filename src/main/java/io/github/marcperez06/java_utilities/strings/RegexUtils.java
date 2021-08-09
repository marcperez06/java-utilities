package io.github.marcperez06.java_utilities.strings;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	private RegexUtils() { }

	/**
	 * Change the character specified to regex that accepts letters and numbers
	 * 
	 * @param text - String to analyze
	 * @param character - String character to change using the reggex
	 * @return String - Regular expresion
	 */
	public static String changeCharToWordRegex(String text, String character) {
		return changeCharToRegex(text, character, "[a-zA-Z_.-]");
	}
	
	/**
	 * Change the character specified to regex that accepts letters and numbers
	 * 
	 * @param text - String to analyze
	 * @param character - String character to change using the reggex
	 * @return String - Regular expresion
	 */
	public static String changeCharToWordAndNumberRegex(String text, String character) {
		return changeCharToRegex(text, character, "[a-zA-Z0-9_.-]");
	}

	/**
	 * Change the character specified to regex that accepts numbers
	 * 
	 * @param text - String to analyze
	 * @param character - String character to change using the reggex
	 * @return String - Regular expresion
	 */
	public static String changeCharToNumericRegex(String text, String character) {
		return changeCharToRegex(text, character, "[0-9]");
	}
	
	private static String changeCharToRegex(String text, String character, String regexForReplacement) {
		String newText = text;
		String regex = "[" + character + "]{1,}";

		while (text.matches(".*" + regex + ".*")) {
			String substring = getSubstringUsingRegEx(newText, regex);
			regex = regexForReplacement + "{1," + substring.length() + "}";
			newText = newText.replaceFirst(substring, regex);
		}

		return newText;
	}

	/**
	 * Return the string part that matches to regular expresion
	 * 
	 * @param text  - String to analyze
	 * @param regex - String regular expression used for analize the text
	 * @return String - Substring of string that matches with regular expression
	 */
	public static String getSubstringUsingRegEx(String text, String regex) {
		String substring = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		MatchResult result = null;

		if (matcher.find()) {
			result = matcher.toMatchResult();
		}

		if (result != null) {
			substring = text.substring(result.start(), result.end());
		} else {
			throw new RuntimeException("Can not apply the regex '" + regex + "' to text '" + text + "'");
		}

		return substring;
	}
	
	/**
	 * Transform text to regex expression
	 * @param text - String
	 * @return String - regex expression
	 */
	public static String toRegex(String text) {
		String regex = text;
		Pattern pattern = toPattern(text);
		
		if (pattern != null) {
			regex = pattern.pattern();
		} else {
			System.out.println("Can not transform '" + text + "' to regex");
		}
		
		return regex;
	}
	
	/**
	 * Transform text to regex expression
	 * @param text - String
	 * @return String - regex expression
	 */
	public static Pattern toPattern(String text) {
		Pattern pattern = null;
		try {
			pattern = Pattern.compile(text);
		} catch (Exception e) {
			System.out.println("Can not create pattern: " + e.getCause().toString());
		}
		return pattern;
	}

}

package io.github.marcperez06.java_utilities.strings;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import io.github.marcperez06.java_utilities.collection.list.ListUtils;

public class StringUtils {

	private static String[] RESERVED_KEYWORDS = {"null", "abstract", "assert", "boolean", "true", "false", "break",
												"byte", "try", "catch", "case", "char", "class", "continue", "default",
												"do", "double", "if", "else", "enum", "exports", "extends", "final",
												"finally", "float", "for", "implements", "import", "instanceof", "int",
												"interface", "long", "module", "native", "new", "package", "private",
												"protected", "public", "return", "requires", "short", "static", "strictfp",
												"super", "synchronized", "this", "throw", "throws", "void", "volatile",
												"while", "const", "goto"};
	
	private static String[] RESERVED_JAVA_TYPES = {"int", "double", "float", "long", "boolean", 
													"Integer", "Double", "Float", "Long", 
													"Object", "String", "Boolean"};
	
	public static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

	private StringUtils() {}
	
	public static boolean isBlank(String text) {
		boolean isBlank = (text == null);
		
		if (!isBlank) {
			isBlank = text.trim().isEmpty();
		}
		
		return isBlank;
	}
	
	public static String capitalizeWord(String word) {
		String capitalize = "";
		if (word != null && !word.isEmpty()) {
			String originalChar = String.valueOf(word.charAt(0));
			String charUperCase = originalChar.toUpperCase();
			capitalize = word.replaceFirst(originalChar, charUperCase);
		}
		return capitalize;
	}
	
	public static String uncapitalizeWord(String word) {
		String uncapitalize = "";
		if (word != null && !word.isEmpty()) {
			String originalChar = String.valueOf(word.charAt(0));
			String charLowerCase = originalChar.toLowerCase();
			uncapitalize = word.replaceFirst(originalChar, charLowerCase);
		}
		return uncapitalize;
	}
	
	/**
	 * Build the string with the params placed in correct site using {x}, %s or ?
	 * example: StringUtils.format("My name is {0} and my last name is {1}", "param0", "param1") 
	 * -- return: "My name is param0 and my last name is param1"
	 * @param text - String that will be formated using params
	 * @param params - Params used for format text
	 * @return String - String formated with params
	 */
	public static String format(String text, String...params) {
		String stringFormated = "";

		int containsStrParams = text.indexOf("%s");
		int containsQuestionParam = text.indexOf("?");
		
		if (containsStrParams > 0) {
			stringFormated = String.format(text, (Object[]) params);
		} else if(containsQuestionParam > 0) {
			stringFormated = String.format(text.replaceAll("\\?", "%s"), (Object[]) params);
		} else {
			stringFormated = formatStringWithBrackets(text, params);
		}

		return stringFormated;
	}
	
	private static String formatStringWithBrackets(String text, String...params) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(text);
		
		for (int i = 0; i < params.length; i++) {
			String param = params[i].trim();
			String separator = "{" + i + "}";
			int indexSeparator = builder.toString().indexOf(separator);
			
			int end = indexSeparator + separator.length();
			
			if (indexSeparator > 0) {
				builder.replace(indexSeparator, end, param);	
			}
		}
		return builder.toString();
	}

	public static <T> boolean addOrAppendStringInMap(Map<T, String> map, T key, String value) {
		boolean success = false;
		if (map != null) {
			if (map.containsKey(key)) {
				appendStringInMap(map, key, value);
			} else {
				map.put(key, value);
				success = true;
			}
		}
		return success;
	}
	
	public static <T> boolean appendStringInMap(Map<T, String> map, T key, String value) {
		boolean success = false;
		String currentValue = "";
		String finalValue = "";
		
		if (map != null) {
			if (map.containsKey(key)) {
				currentValue = map.get(key);
				finalValue = currentValue + " " + value;
				map.put(key, finalValue);
				success = true;
			}
		}
		return success;
	}
	
	public static boolean listContainsString(List<String> list, String string) {
		boolean contains = false;
		for (int i = 0; i < list.size() && !contains; i++) {
			String word = list.get(i);
			contains = word.contains(string);
		}
		return contains;
	}
	
	public static boolean arrayContainsString(String[] array, String string) {
		boolean contains = false;
		for (int i = 0; i < array.length && !contains; i++) {
			String word = array[i];
			contains = word.contains(string);
		}
		return contains;
	}
	
	public static boolean valueContainsAnyWord(String value, String...words) {
		boolean containsWord = false;
		if (words.length > 0) {
			for (int i = 0; i < words.length && !containsWord; i++) {
				String word = words[i];
				containsWord = value.contains(word);
			}
		}
		return containsWord;
	}
	
	public static List<String> processSimpleText(String text) {
        List<String> tokens = Arrays.asList(text.toLowerCase().replaceAll("[^a-z0-9']", " ").split("\\s+"));

        ArrayList<String> terms = new ArrayList<String>();
        for (String token : tokens) {
            
        	String clearedToken = token.trim();
        	
        	if (!clearedToken.isEmpty()) {
        		terms.add(token);	
        	}

        }

        return terms;
    }
	
	public static List<String> processSimpleTextRemovingStopWords(String text, List<String> stopWords) {
        String clearText = text.toLowerCase().replaceAll("[^a-z0-9']", " ");
        clearText = clearText.replaceAll("'", " ");
		List<String> tokens = Arrays.asList(clearText.split("\\s+"));

        ArrayList<String> terms = new ArrayList<String>();
        for (String token : tokens) {
        	
    		if (!ListUtils.existObjectInList(stopWords, token)) {
        		terms.add(token);
    		}

        }

        return terms;
    }
	
	public static String[] tokenize(String txt, String splitter) {
		StringTokenizer tokenizer = new StringTokenizer(txt, splitter);
		String[] result = new String[tokenizer.countTokens()];
		List<String> tokenize = new ArrayList<String>();

		 while(tokenizer.hasMoreTokens()){
			 String token = tokenizer.nextToken();
			 if (token != null) {
				 tokenize.add(token);
			 }
         }
		
		return tokenize.toArray(result);
	}
	
	public static List<String> splitList(String txt, String splitter) {
		return Arrays.asList(split(txt, splitter));
	}
	
	public static String[] split(String txt, String splitter) {
		try {
			return txt.split(splitter);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String concatArrayOfString(String[] array, String concatener) {
		StringBuilder builder = new StringBuilder();
		String concat = "";
		
		if (array != null) {
			
			for (String value : array) {
				builder.append(value);
				builder.append(concatener);
			}
			
			concat = cutStringWithOtherString(builder.toString(), concatener, 0);
		}
		return concat;
	}
	
	public static String concatListOfString(List<String> list, String concatener) {
		StringBuilder builder = new StringBuilder();
		String concat = "";
		
		if (list != null) {
			for (String value : list) {
				builder.append(value);
				builder.append(concatener);
			}
			
			concat = cutStringWithOtherString(builder.toString(), concatener, 0);
		}
		return concat;
	}
	
	public static String cutStringWithOtherString(String txt, String otherText, int start) {
		String cutText = txt;
		if (txt != null && !txt.isEmpty()) {
			int end = txt.length() - otherText.length();
			cutText = txt.substring(start, end);
		}
		return cutText;
	}

	public static String stripDiacritics(String str) {
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
		return str;
	}

	public static String clearSpecialCharactersWithoutWhiteSpace(String text) {
		String clearedText = "";
		if (text != null && !text.isEmpty()) {
			clearedText = text.replaceAll("[^a-zA-Z0-9]", "");
		}
		return clearedText;
	}
	
	public static String clearSpecialCharacters(String text) {
		String clearedText = "";
		if (text != null && !text.isEmpty()) {
			clearedText = text.replaceAll("[^a-zA-Z0-9\\s+]", "");
		}
		return clearedText;
	}
	
	public static List<String> returnListInLowerCase(List<String> list) {
		ArrayList<String> listLower = new ArrayList<String>();
		for (String str : list) {
			listLower.add(str.toLowerCase());
		}
		return listLower;
	}
	
	public static String[] returnArrayInLowerCase(String[] array) {
		String[] arr = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			arr[i] = array[i].toLowerCase();
		}
		return arr;
	}
	
	public static boolean isReservedKeyword(String word) {
		boolean isReserved = false;
		for (int i = 0; i < RESERVED_KEYWORDS.length && !isReserved; i++) {
			isReserved = word.equals(RESERVED_KEYWORDS[i]);
		}
		return isReserved;
	}
	
	public static boolean isJavaVariableType(String variableType) {
		boolean isJavaVariableType = false;
		if (!isJavaVariableType) {
			for (int i = 0; i < RESERVED_JAVA_TYPES.length && !isJavaVariableType; i++) {
				isJavaVariableType = variableType.equals(RESERVED_JAVA_TYPES[i]);
			}
		}
		return isJavaVariableType;
	}
	
}
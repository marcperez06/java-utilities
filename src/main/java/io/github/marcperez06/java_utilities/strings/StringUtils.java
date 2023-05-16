package io.github.marcperez06.java_utilities.strings;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import io.github.marcperez06.java_utilities.collection.list.ListUtils;

public class StringUtils {

	private static final String UTF_8 = "UTF-8";
	
	private static final String[] RESERVED_KEYWORDS = {"null", "abstract", "assert", "boolean", "true", "false", "break",
												"byte", "try", "catch", "case", "char", "class", "continue", "default",
												"do", "double", "if", "else", "enum", "exports", "extends", "final",
												"finally", "float", "for", "implements", "import", "instanceof", "int",
												"interface", "long", "module", "native", "new", "package", "private",
												"protected", "public", "return", "requires", "short", "static", "strictfp",
												"super", "synchronized", "this", "throw", "throws", "void", "volatile",
												"while", "const", "goto"};
	
	private static final String[] RESERVED_JAVA_TYPES = {"int", "double", "float", "long", "boolean", 
														"Integer", "Double", "Float", "Long", 
														"Object", "String", "Boolean"};
	
	public static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}\\p{ASCII}]+");

	private StringUtils() {}
	
	public static boolean isBlank(String text) {
		boolean isBlank = (text == null);
		
		if (!isBlank) {
			isBlank = text.trim().isEmpty();
		}
		
		return isBlank;
	}
	
	/**
	 * Capitalize the word specified in the param
	 * @param word - String word
	 * @return String - Word capaitalized
	 */
	public static String capitalizeWord(String word) {
		String capitalize = "";
		if (word != null && !word.isEmpty()) {
			String originalChar = String.valueOf(word.charAt(0));
			String charUperCase = originalChar.toUpperCase();
			capitalize = word.replaceFirst(originalChar, charUperCase);
		}
		return capitalize;
	}
	
	/**
	 * Uncapitalize the word specified in the param
	 * @param word - String word
	 * @return String - Word uncapaitalized
	 */
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
	
	public static boolean valueContainsAnyWord(String value, List<String> words) {
		boolean containsWord = false;
		if (words != null) {
			containsWord = valueContainsAnyWord(value, words.toArray(new String[words.size()]));
		}
		return containsWord;
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

	public static String stripDiacritics(String text) {
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		text = DIACRITICS_AND_FRIENDS.matcher(text).replaceAll("");
		return text;
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
	
	public static String toPascalCase(String sentence) {
		sentence = stripDiacritics(sentence);
		sentence = clearSpecialCharacters(sentence);
		StringBuilder pascalCase = new StringBuilder();
		String[] words = split(sentence, " ");
		for (String word : words) {
			word = wordToPascalCase(word);
			pascalCase.append(word);
		}
		return pascalCase.toString();
	}
	
	public static String wordToPascalCase(String word) {
		String wordPascalCase = word;
		if (!isBlank(word)) {
			wordPascalCase = stripDiacritics(word);
			wordPascalCase = capitalizeWord(word.toLowerCase());
		}
		return wordPascalCase;
	}
	
	public static String toCamelCase(String sentence) {
		sentence = stripDiacritics(sentence);
		sentence = clearSpecialCharacters(sentence);
		StringBuilder camelCase = new StringBuilder();
		String[] words = split(sentence, " ");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (i == 0) {
				word = stripDiacritics(word);
				word = word.toLowerCase();
			} else {
				word = wordToPascalCase(word);
			}
			camelCase.append(word);
		}
		return camelCase.toString();
	}
	
	public static String encode(String text) {
		String encodedText = "";
		if (!isBlank(text)) {
			try {
				encodedText = URLEncoder.encode(text, UTF_8);
			} catch (Exception e) { }
		}
		return encodedText;
	}
	
	public static String decode(String text) {
		String decodedText = "";
		// decodedText = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		if (!isBlank(text)) {
			try {
				decodedText = URLDecoder.decode(text, UTF_8);
			} catch (Exception e) { }
		}
		return decodedText;
	}

}
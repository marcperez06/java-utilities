/**
 * @author Marc Perez Rodriguez
 */
package io.github.marcperez06.java_utilities.testdata;

import java.util.Random;

public class RandomDataGenerator {
	
	private static final String CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARS_UPPER = CHARS_LOWER.toUpperCase();
    private static final String CHARS = CHARS_LOWER + CHARS_UPPER;
    private static final String NUMBERS = "0123456789";
    private static final String CHARS_WITH_NUMBERS = CHARS + NUMBERS;
    
    public static String getStringWithNumbers(int length) {
        return generateRandomString(CHARS_WITH_NUMBERS, length);
    }
    
    public static String getString(int length) {
        return generateRandomString(CHARS, length);
    }
    
    public static String getStringInLowerCase(int length) {
        return generateRandomString(CHARS_LOWER, length);
    }
    
    public static String getStringInUpperCase(int length) {
        return generateRandomString(CHARS_UPPER, length);
    }
    
    public static String getStringWithOnlyNumbers(int length) {
        return generateRandomString(NUMBERS, length);
    }
    
    public static Integer getInteger(int length) {
        return Integer.valueOf(generateRandomString(NUMBERS, length));
    }
    
    public static Double getDouble(int length) {
        return getDouble(length, 0);
    }
    
    public static Double getDouble(int wholeLength, int decimalLength) {
    	Double whole = Double.valueOf(0);
    	if (wholeLength > 0) {
    		whole = Double.valueOf(generateRandomString(NUMBERS, wholeLength));
    	}
    	
    	Double decimal = Double.valueOf(0);
    	if (decimalLength > 0) {
    		decimal = Double.valueOf("0." + generateRandomString(NUMBERS, decimalLength));	
    	}
        return whole + decimal;
    }
    
    public static Long getLong(int length) {
        return getLong(length, 0);
    }
    
    public static Long getLong(int wholeLength, int decimalLength) {
    	Long whole = Long.valueOf(0);
    	if (wholeLength > 0) {
    		whole = Long.valueOf(generateRandomString(NUMBERS, wholeLength));
    	}
    	
    	Long decimal = Long.valueOf(0);
    	if (decimalLength > 0) {
    		Long.valueOf("0." + generateRandomString(NUMBERS, decimalLength));
    	}
        return whole + decimal;
    }
    
    private static String generateRandomString(String source, int length ) {
    	Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        
        if (length > 0) {
        	
        	for (int i = 0; i < length; i++) {
        		int randomCharIndex = random.nextInt(source.length());
        		char randomChar = source.charAt(randomCharIndex);
        		stringBuilder.append(randomChar);
        	}
        	
        }
        
        return stringBuilder.toString();
    }
    
	public static int getIntBetween(int start, int end) {
		int randomIndex = 0;
		Random random = new Random();
		int[] minMax = getMinMax(start, end);
		int randomBoundary = 0;
		int sign = sign(minMax[0]);

		if (sign == 1) {
			randomBoundary = end - start;
		} else {
			randomBoundary = end + start;
		}
		
		randomIndex = random.nextInt(randomBoundary) + (start * sign);
		
		return randomIndex;
	}
	
	private static int[] getMinMax(int start, int end) {
		int[] minMax = new int[2];
		if (start <= end) {
			minMax[0] = start;
			minMax[1] = end;
		} else {
			minMax[0] = end;
			minMax[1] = start;
		}
		return minMax;
	}
	
	private static int sign(int num) {
		int sign = -1;
		if (num >= 0) {
			sign = 1;
		}
		return sign;
	}

}
package io.github.marcperez06.java_utilities.testdata;

import java.util.Random;

public class RandomDataGenerator {
	
	private static final String CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARS_UPPER = CHARS_LOWER.toUpperCase();
    private static final String CHARS = CHARS_LOWER + CHARS_UPPER;
    private static final String NUMBERS = "0123456789";
    private static final String CHARS_WITH_NUMBERS = CHARS + NUMBERS;
    
    public static String getStringWithNumbers(int length) {
        return geenerateRandomString(CHARS_WITH_NUMBERS, length);
    }
    
    public static String getString(int length) {
        return geenerateRandomString(CHARS, length);
    }
    
    public static String getStringInLowerCase(int length) {
        return geenerateRandomString(CHARS_LOWER, length);
    }
    
    public static String getStringInUpperCase(int length) {
        return geenerateRandomString(CHARS_UPPER, length);
    }
    
    public static String getStringWithOnlyNumbers(int length) {
        return geenerateRandomString(NUMBERS, length);
    }
    
    private static String geenerateRandomString(String source, int length ) {
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
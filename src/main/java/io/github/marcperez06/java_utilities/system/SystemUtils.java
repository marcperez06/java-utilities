package io.github.marcperez06.java_utilities.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemUtils {
    
    private static String readLine(String message) {
    	String readLine = "";
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println(message);
    	try {
    		readLine = reader.readLine();
    	} catch (IOException e) {
    		System.out.println("Can not read what you write, reading as empty");
    	}
    	return readLine;
    }

}

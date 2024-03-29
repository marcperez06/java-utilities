package io.github.marcperez06.java_utilities.logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Logger {
	
	private static final String START_STACK_TRACE_INFORMATION = "+++++ Stack Trace Information +++++";
	private static final String END_STACK_TRACE_INFORMATION = "+++++++++++++++++++++++++++++++++++";
	private static final String FILE_NAME = "++ File Name: ";
	private static final String CLASS_NAME = "++ Class Name: ";
	private static final String METHOD_NAME = "++ Method Name: ";
	private static final String ACTION = "------- ACTION -----";
	private static final String ERROR = "------- ERROR ------";
	
	private Logger() {}
	
	/**
	 * Only print messages if the System property "java.utilities.debug.messages.enabled" is defined as "true" in String format
	 * @param message - String
	 */
	public static void debug(String message) {
		String property = "java.utilities.debug.messages.enabled";
		boolean debugMessagesEnabled = System.getProperty(property) != null && System.getProperty(property).equals("true");
		if (debugMessagesEnabled) {
			System.out.println(message);	
		}
	}
	
	/**
	 * Print message using System.out.println()
	 * @param message - String
	 */
	public static void println(String message) {
		System.out.println(message);
	}
	
	/**
	 * Print message and the current date time
	 * @param message - String
	 */
	public static void forceLog(String message) {
		String date = getCurrentTime();
		println("- " + date + " --> " + message);
	}
	
	/**
	 * Print message and the current date time, only if debug messages are enabled "java.utilities.debug.messages.enabled='true'"
	 * @param message - String
	 */
	public static void log(String message) {
		String date = getCurrentTime();
		debug("- " + date + " --> " + message);
	}
	
	private static void printStartEnd() {
		System.out.println("//////////////////////////////////////////////////////////////");
	}
	
	private static void printDate() {
		String date = getCurrentTime();
		log("---- Log registred: " + date + " ----");
	}
	
	private static void printStackTraceInfo() {
		StackTraceElement stackTrace = getCallerClass();
		printStartEnd();
		printDate();
		if (stackTrace != null) {
			String methodName = stackTrace.getMethodName();
			String className = stackTrace.getClassName();
			String fileName = stackTrace.getFileName();
			
			log(START_STACK_TRACE_INFORMATION);
			log(FILE_NAME + fileName);
			log(CLASS_NAME + className);
			log(METHOD_NAME + methodName);
			log(END_STACK_TRACE_INFORMATION);
		}
		
	}
	
	private static StackTraceElement getCallerClass() {
		Thread currentThread = Thread.currentThread();
		StackTraceElement[] stackTrace = currentThread.getStackTrace();
		return stackTrace[5]; // 0 = This method...
	}
	
	private static String getCurrentTime() {
		Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
		Date today = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		String date = dateFormat.format(today);
		return date;
	}
	
	/**
	 * Print Action message and stack trace, only if debug messages are enabled "java.utilities.debug.messages.enabled='true'"
	 * @param message - String
	 */
	public static void action(String message) {
		printStackTraceInfo();
		log(ACTION);
		log(message);
		printStartEnd();
	}
	
	/**
	 * Print Error message and stack trace, 
	 * only print stack trace if debug messages are enabled "java.utilities.debug.messages.enabled='true'"
	 * @param message - String
	 */
	public static void error(String message) {
		printStartEnd();
		forceLog(ERROR);
		forceLog(message);
		printStartEnd();
	}

}

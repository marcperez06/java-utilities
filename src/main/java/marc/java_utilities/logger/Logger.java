package marc.java_utilities.logger;

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
	
	public static void print(String message) {
		String date = getCurrentTime();
		System.out.print("- " + date + " --> " + message);
	}
	
	public static void println(String message) {
		String date = getCurrentTime();
		System.out.println("- " + date + " --> " + message);
	}
	
	private static void printStartEnd() {
		System.out.println("//////////////////////////////////////////////////////////////");
	}
	
	private static void printDate() {
		String date = getCurrentTime();
		println("---- Log registred: " + date + " ----");
	}
	
	private static void printStackTraceInfo() {
		StackTraceElement stackTrace = getCallerClass();
		printStartEnd();
		printDate();
		if (stackTrace != null) {
			String methodName = stackTrace.getMethodName();
			String className = stackTrace.getClassName();
			String fileName = stackTrace.getFileName();
			
			println(START_STACK_TRACE_INFORMATION);
			println(FILE_NAME + fileName);
			println(CLASS_NAME + className);
			println(METHOD_NAME + methodName);
			println(END_STACK_TRACE_INFORMATION);
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
	
	public static void action(String message) {
		printStackTraceInfo();
		println(ACTION);
		println(message);
		printStartEnd();
	}
	
	public static void error(String message) {
		printStartEnd();
		println(ERROR);
		println(message);
		printStartEnd();
	}

}

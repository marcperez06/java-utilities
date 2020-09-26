package io.github.marcperez06.java_utilities.servlets;

import javax.servlet.http.HttpServletRequest;

public class ServletUtilities {
	
	private ServletUtilities() {} // Uninstantiatable class

	public static String headWithTitle(String title) {
		return ("<!DOCTYPE html>\n" + "<html>\n" + "<head><title>" + title + "</title></head>\n");
	}

	/**
	 * Read a parameter with the specified name, convert it to an int, and return
	 * it. Return the designated default value if the parameter doesn't exist or if
	 * it is an illegal integer format.
	 * @param request - HttpServletRequest
	 * @param paramName - String
	 * @param defaultValue - int
	 * @return int - Parameter value
	 */
	public static int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
		String paramString = request.getParameter(paramName);
		int paramValue;
		try {
			paramValue = Integer.parseInt(paramString);
		} catch (Exception nfe) { // null or bad format
			paramValue = defaultValue;
		}
		return (paramValue);
	}

	/**
	 * Reads param and converts to double. Default if problem.
	 * @param request - HttpServletRequest
	 * @param paramName - String
	 * @param defaultValue - double
	 * @return double - Parameter value
	 */
	public static double getDoubleParameter(HttpServletRequest request, String paramName, double defaultValue) {
		String paramString = request.getParameter(paramName);
		double paramValue;
		try {
			paramValue = Double.parseDouble(paramString);
		} catch (Exception nfe) { // null or bad format
			paramValue = defaultValue;
		}
		return (paramValue);
	}

	/**
	 * Replaces characters that have special HTML meanings with their corresponding
	 * HTML character entities. Specifically, given a string, this method replaces
	 * all occurrences of {@literal
	 *  '<' with '&lt;', all occurrences of '>' with
	 *  '&gt;', and (to handle cases that occur inside attribute
	 *  values), all occurrences of double quotes with
	 *  '&quot;' and all occurrences of '&' with '&amp;'.
	 *  Without such filtering, an arbitrary string
	 *  could not safely be inserted in a Web page.
	 *  }
	 * @param input - String
	 * @return String - Input filtered
	 */
	public static String filter(String input) {
		String filteredInput = input;
		
		if (hasSpecialChars(input)) {
			
			StringBuilder filtered = new StringBuilder(input.length());
			
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				
				if (c == '<') {
					filtered.append("&lt;");
				} else if (c == '>') {
					filtered.append("&gt;");
				} else if (c == '"') {
					filtered.append("&quot;");
				} else if (c == '&') {
					filtered.append("&amp;");
				} else {
					filtered.append(c);
				}

			}
			filteredInput = filtered.toString();
		}
		
		return filteredInput;
	}

	private static boolean hasSpecialChars(String input) {
		boolean haveSpecialChar = false;
		
		if (input != null && !input.isEmpty()) {
			
			for (int i = 0; i < input.length() && !haveSpecialChar; i++) {
				char c = input.charAt(i);
				
				if (c == '<') {
					haveSpecialChar = true;
				} else if (c == '>') {
					haveSpecialChar = true;
				} else if (c == '"') {
					haveSpecialChar = true;
				} else if (c == '&') {
					haveSpecialChar = true;
				}
				
			}

		}
		
		return haveSpecialChar;
	}

}
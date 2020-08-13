/**
 * @author Marc Perez Rodriguez
 */
package marc.java_utilities.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Servlet extends HttpServlet {

	public Servlet() {}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	// Need Override.
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	// Need Override.
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void setResponseHTMLHeader(HttpServletResponse response) {
		response.setContentType("text/html");
	}

	protected void setResponseJSONHeader(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
	}

	/**
	 * Method that modify the response headers for add a PDF file, in attachment or
	 * inline way
	 * 
	 * @param response - HttpServletResponse
	 * @param pdf      - File
	 * @param type     - String (attachment or inline)
	 */
	protected void setResponsePDF(HttpServletResponse response, File pdf, String type) {
		if (pdf != null) {
			if (type.equals("attachment") || type.equals("inline")) {
				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition", type + "; filename=" + pdf);
				response.setContentLength((int) pdf.length());
			}
		}
	}

	protected void sendJsonResponse(HttpServletRequest request, HttpServletResponse response, 
										String jsonResponse) throws ServletException, IOException {
		
		this.setResponseJSONHeader(response);
		response.getWriter().print(jsonResponse);
	}

}

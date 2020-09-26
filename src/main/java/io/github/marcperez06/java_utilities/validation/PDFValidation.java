package io.github.marcperez06.java_utilities.validation;

import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;

import io.github.marcperez06.java_utilities.pdf.PdfParsed;

public class PDFValidation {
	
	private static final String INVALID_CARACTERS = "Ã";
	
	private static final byte UTF8_TO_CLASS[] = {
												  // The first part of the table maps bytes to character classes that
												  // to reduce the size of the transition table and create bitmasks.
												  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
												  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
												  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
												  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
												  1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,  9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,
												  7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,  7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
												  8,8,2,2,2,2,2,2,2,2,2,2,2,2,2,2,  2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
												  10,3,3,3,3,3,3,3,3,3,3,3,3,4,3,3, 11,6,6,6,5,8,8,8,8,8,8,8,8,8,8,8
												};

	private static final byte UTF8_TRANSITION[] = {
													 // The second part is a transition table that maps a combination
													 // of a state of the automaton and a character class to a state.
													  0,12,24,36,60,96,84,12,12,12,48,72, 12,12,12,12,12,12,12,12,12,12,12,12,
													  12, 0,12,12,12,12,12, 0,12, 0,12,12, 12,24,12,12,12,12,12,24,12,24,12,12,
													  12,12,12,12,12,12,12,24,12,12,12,12, 12,24,12,12,12,12,12,12,12,24,12,12,
													  12,12,12,12,12,12,12,36,12,36,12,12, 12,36,12,12,12,12,12,36,12,36,12,12,
													  12,36,12,12,12,12,12,12,12,12,12,12, 
													  // padding so we get at 512 and the compiler can determine
													  // that there is no overflow
													  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
													  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
													  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
													  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
													  0, 0, 0, 0,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
													  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
													};

	public static boolean isPdf(URL url) {
		return isPdf(url.toString());
	}
	
	public static boolean isPdf(String path) {
		boolean isPdf = false;
		if (path != null) {
			isPdf = path.toString().endsWith(".pdf");
		}
		return isPdf;
	}
	
	public static boolean isASCIIEncoded(String pdfPath) {
		PdfParsed pdfParsed = new PdfParsed(pdfPath);
		return isASCIIEncoded(pdfParsed);
	}
	
	public static boolean isASCIIEncoded(URL pdfUrl) {
		PdfParsed pdfParsed = new PdfParsed(pdfUrl);
		return isASCIIEncoded(pdfParsed);
	}
	
	private static boolean isASCIIEncoded(PdfParsed pdfParsed) {
		boolean isAscii = false;
		
		if (pdfParsed != null && !pdfParsed.getParsedContent().isEmpty()) {
			byte[] bytes = pdfParsed.getParsedContent().getBytes();
			boolean notIsAscii = false;
			
			for (int i = 0; i < bytes.length && !notIsAscii; i++) {
				if (bytes[i] < 0) {
					notIsAscii = true;
				}
			}
			
			isAscii = !notIsAscii;
			
		}

	    return isAscii;
	}
	
	public static boolean isUTF8Encoded(String pdfPath) {
		PdfParsed pdfParsed = new PdfParsed(pdfPath);
		return isUTF8Encoded(pdfParsed);
	}
	
	public static boolean isUTF8Encoded(URL pdfUrl) {
		PdfParsed pdfParsed = new PdfParsed(pdfUrl);
		return isUTF8Encoded(pdfParsed);
	}
	
	public static boolean isUTF8Encoded(PdfParsed pdfParsed) {
		boolean isUtf8 = false;
		
		if (pdfParsed != null && !pdfParsed.getParsedContent().isEmpty()) {
			
			try {
				
				byte[] bytes = pdfParsed.getParsedContent().getBytes("UTF-8");
				int length = bytes.length;
				int chunk = length / 4;
				int checkFirst = 0;
				int checkSecond = 0;
				int checkThird = 0;
				int checkLast = 0;
				
				for (int i = 0, j = chunk, k = 2 * chunk, l = 3 * chunk; i < chunk; i++, j++, k++, l++) {
					checkFirst = UTF8_TRANSITION[(checkFirst + (UTF8_TO_CLASS[bytes[i] & 0xFF])) & 0xFF];
					checkSecond = UTF8_TRANSITION[(checkSecond + (UTF8_TO_CLASS[bytes[j] & 0xFF])) & 0xFF];
					checkThird = UTF8_TRANSITION[(checkThird + (UTF8_TO_CLASS[bytes[k] & 0xFF])) & 0xFF];
					checkLast = UTF8_TRANSITION[(checkLast + (UTF8_TO_CLASS[bytes[l] & 0xFF])) & 0xFF];
				}
				
				isUtf8 = (checkFirst == 0 && checkSecond == 0);
				isUtf8 &= (checkThird == 0 && checkLast == 0);
				
			} catch (UnsupportedEncodingException e) {
				isUtf8 = false;
			}
			
		}
		
		return isUtf8;
	}
	
	public static boolean allContentIsValid(String pdfPath) {
		PdfParsed pdfParsed = new PdfParsed(pdfPath);
		return allContentIsValid(pdfParsed);
	}
	
	public static boolean allContentIsValid(URL pdfUrl) {
		PdfParsed pdfParsed = new PdfParsed(pdfUrl);
		return allContentIsValid(pdfParsed);
	}
	
	public static boolean allContentIsValid(PdfParsed pdfParsed) {
		boolean allContentIsValid = false;
		if (pdfParsed != null) {
			allContentIsValid = true;
			char[] chars = pdfParsed.getParsedContentInCharArrayFormat();
			
			if (chars != null) {
				for (int i = 0; i < chars.length && allContentIsValid; i++) {
					String strChar = String.valueOf(chars[i]);
					allContentIsValid = !INVALID_CARACTERS.contains(strChar);
				}	
			}
		}
		return allContentIsValid;
	}
	
	public static boolean containsText(String pdfPath, String text) {
		PdfParsed pdfParsed = new PdfParsed(pdfPath);
		return containsText(pdfParsed, text);
	}
	
	public static boolean containsText(URL pdfUrl, String text) {
		PdfParsed pdfParsed = new PdfParsed(pdfUrl);
		return containsText(pdfParsed, text);
	}
	
	public static boolean containsText(PdfParsed pdfParsed, String text) {
		boolean containsText = false;
		
		if (pdfParsed != null) {
		
			String parsedText = pdfParsed.getParsedContent();
			
			if (!parsedText.isEmpty()) {
				containsText = parsedText.contains(text);
			}
			
		}
		
		return containsText;		
	}
	
	public static boolean validatePdf(String path) {
		boolean isValid = false;
		ValidationResult result = null;

		try {
			PreflightParser parser = new PreflightParser(path);
			parser.parse();

			PreflightDocument document = parser.getPreflightDocument();
			document.validate();

			// Get validation result
			result = document.getResult();
			document.close();

		} catch (SyntaxValidationException e) {
			result = e.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// display validation result
		if (result != null && result.isValid()) {
			isValid = true;
		} else if (result != null) {
			System.out.println("The file" + path + " is not valid, error(s) :");
			for (ValidationError error : result.getErrorsList()) {
				System.out.println(error.getErrorCode() + " : " + error.getDetails());
			}
			isValid = false;
		}

		return isValid;
	}
	
}
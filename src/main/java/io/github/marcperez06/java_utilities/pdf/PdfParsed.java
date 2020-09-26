package io.github.marcperez06.java_utilities.pdf;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfParsed {
	
	private String urlOrPath;
	private URL url;
	private PDDocument document;
	private String parsedHeader;
	private String parsedContent;
	private String parsedFooter;
	private List<BufferedImage> parsedImages;
	private boolean isParsed;
	
	public PdfParsed(String urlOrPath) {
		try {
			this.urlOrPath = urlOrPath;
			this.parsePdf();
		} catch (Exception e) {
			this.isParsed = false;
			e.printStackTrace();
		}
	}
	
	public PdfParsed(URL url) {
		this.setUrl(url);
	}
	
	public boolean isParsed() {
		return this.isParsed;
	}
	
	public URL getUrl() {
		return this.url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
		this.urlOrPath = url.toString();
		this.parsePdf();
	}
	
	public String getStringUrl() {
		return this.url.toString();
	}
	
	public String getParsedHeader() {
		String parsedHeader = "";
		if (this.isParsed) {
			parsedHeader = this.parsedHeader;
		} else {
			System.out.println("PDF not is parsed, for this reason, return empty string");
		}
		return parsedHeader;
	}
	
	public String getParsedContent() {
		String parsedText = "";
		if (this.isParsed) {
			parsedText = this.parsedContent;
		} else {
			System.out.println("PDF not is parsed, for this reason, return empty string");
		}
		return parsedText;
	}
	
	public char[] getParsedContentInCharArrayFormat() {
		char[] chars = null;
		if (this.parsedContent != null && !this.parsedContent.isEmpty()) {
			this.parsedContent = this.parsedContent.replaceAll("\r", "");
			this.parsedContent = this.parsedContent.replaceAll("\t", "");
			this.parsedContent = this.parsedContent.replaceAll("\n", "");
			this.parsedContent = this.parsedContent.replaceAll("\\s*", "");
			this.parsedContent = this.parsedContent.trim();
			chars = this.parsedContent.toCharArray();
		}
		return chars;
	}
	
	public String getParsedFooter() {
		String parsedFooter = "";
		if (this.isParsed) {
			parsedFooter = this.parsedFooter;
		} else {
			System.out.println("PDF not is parsed, for this reason, return empty string");
		}
		return parsedFooter;
	}
	
	public String getParsedText() {
		String parsedText = "";
		if (this.isParsed) {
			parsedText += this.parsedHeader;
			parsedText += this.parsedContent;
			parsedText += this.parsedFooter;
		} else {
			System.out.println("PDF not is parsed, for this reason, return empty string");
		}
		return parsedText;
	}
	
	public PDDocumentInformation getDocumentInformation() {
		PDDocumentInformation info = null;
		if (this.document != null) {
			info = this.document.getDocumentInformation();
		}
		return info;
	}
	
	private void parsePdf() {
		if (this.urlOrPath != null) {
			try {
				
				this.createDocument(this.urlOrPath);
				
				if (this.document != null) {
					
					PDFTextStripper pdfStripper = new PDFTextStripper();
					this.parsedContent = pdfStripper.getText(this.document);
					this.parsedHeader = pdfStripper.getParagraphStart();
					this.parsedFooter = pdfStripper.getParagraphEnd();
					
					this.isParsed = true;
				}

			} catch (Exception e) {
				e.printStackTrace();
				this.parsedContent = "";
			} finally {
				this.closePdfDocument(this.document);
			}
		}
	}
	
	private void createDocument(String urlOrPath) throws MalformedURLException {
		if (urlOrPath.startsWith("http") || urlOrPath.startsWith("www")) {
			this.createDocumentFromUrl(new URL(urlOrPath));
		} else {
			this.createDocumentFromFile(urlOrPath);
		}
	}
	
	private void createDocumentFromUrl(URL url) {
		try (BufferedInputStream file = new BufferedInputStream(this.url.openStream())) {
			this.document = PDDocument.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createDocumentFromFile(String path) {
		try {
			File file = new File(path);
			this.document = PDDocument.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void closePdfDocument(PDDocument document) {
		try {
			if (document != null) {
				document.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

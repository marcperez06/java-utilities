package io.github.marcperez06.java_utilities.api.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Optional;

import io.github.marcperez06.java_utilities.api.request.credentials.RequestCredentials;
import io.github.marcperez06.java_utilities.api.rest.exceptions.RestClientException;
import io.github.marcperez06.java_utilities.api.rest.interfaces.IRestClient;
import io.github.marcperez06.java_utilities.logger.Logger;
import io.github.marcperez06.java_utilities.testdata.RandomDataGenerator;

public abstract class BaseRestClient implements IRestClient {
	
	protected boolean useCertificate;
    protected boolean useProxy;
    protected Optional<String> certificateFilePath;
    protected Optional<String> certificateFilePassword;

	public BaseRestClient() {
		this.useCertificate = false;
    	this.useProxy = false;
    	this.certificateFilePath = Optional.empty();
    	this.certificateFilePassword = Optional.empty();
	}
	
	public abstract void useCertificate();
	
	@Override
    public void setCertificate(String certificateFilePath, String certificateFilePassword) {
        this.certificateFilePath = Optional.of(certificateFilePath);
        this.certificateFilePassword = Optional.of(certificateFilePassword);
        this.useCertificate();
    }
	
	@Override
	public File downloadFile(String url) {
		String fileName = RandomDataGenerator.getStringInLowerCase(5);
		return this.downloadFile(url, fileName);
	}
	
	@Override
	public File downloadFile(String url, String fileName) {
		//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        //LocalDateTime now = LocalDateTime.now();
		String filePath = new File("").getAbsolutePath();
		String savePath = filePath + File.separator + "downloads" + File.separator + fileName;
		return this.downloadFile(url, fileName, savePath);
	}
	
	@Override
	public File downloadFile(String url, String fileName, String savePath) {
		return this.downloadFile(url, fileName, savePath, null);
	}
	
	@Override
	public File downloadFile(String url, String fileName, RequestCredentials credentials) {
		String filePath = new File("").getAbsolutePath();
		String savePath = filePath + File.separator + "downloads" + File.separator + fileName;
		return this.downloadFile(url, fileName, savePath, credentials);
	}
	
	@Override
	public File downloadFile(String urlStr, String fileName, String savePath, RequestCredentials credentials) {
		File file = null;
		
		try {
			URL url = new URL(urlStr);
			URLConnection urlConnection = url.openConnection();
			this.setUrlConnectionCredentials(urlConnection, credentials);
			
			boolean writeFile = this.writeFile(urlConnection, savePath);
			
			if (writeFile) {
				file = new File(savePath);
			} else {
				Logger.println("Can not download the file from url: " + url);
			}

		} catch (Exception e) {
			throw new RestClientException("Can not download the File", e);
		}
		
		return file;
	}
	
	private void setUrlConnectionCredentials(URLConnection urlConnection, RequestCredentials credentials) {
		if (credentials != null && credentials.haveCredentials()) {
        	String auth = "";
        	
        	if (credentials.haveUserCredentials()) {
        		String userPass = credentials.getUser() + ":" + credentials.getPassword();
        		byte[] encode = Base64.getEncoder().encode(userPass.getBytes());
                auth = "Basic " + new String(encode);
        	} else {
        		auth = "Bearer " + credentials.getToken();
        	}
        	
        	urlConnection.setRequestProperty ("Authorization", auth);	
        }
	}
	
	private boolean writeFile(URLConnection urlConnection, String savePath) {
		boolean writeFile = false;
		byte[] buffer = new byte[1024];
		int count = 0;
		
		try {
			
			InputStream input = urlConnection.getInputStream();
			FileOutputStream file = new FileOutputStream(savePath);

			while ((count = input.read(buffer, 0, 1024)) != -1) {
				file.write(buffer, 0, count);
			}

			file.close();
			input.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			writeFile = false;
		}

		return writeFile;
	}

}
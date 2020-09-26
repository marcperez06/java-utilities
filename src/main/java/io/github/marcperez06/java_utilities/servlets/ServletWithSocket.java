/**
 * @author Marc Perez Rodriguez
 */
package io.github.marcperez06.java_utilities.servlets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServletWithSocket {

	private String host;
	private int port;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	private Socket socket;
	
	public ServletWithSocket() {}
	public ServletWithSocket(String host, int port) {
		try {
			this.host = host;
			this.port = port;
			this.setSocket(this.createSocket(host, port));
		} catch (Exception e) {
			e.printStackTrace();
			this.closeBuffer();
			this.closeWriter();
			this.closeSocket();
		}
	}
	
	// --- GETTERS ---
	public String getHost() {
		return this.host;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public BufferedReader getBufferedReader() {
		return this.bufferedReader;
	}
	
	public PrintWriter getPrintWriter() {
		return this.printWriter;
	}
	
	// --- SETTERS ---
	public void setSocket(Socket socket) {
		try {
			if (socket != null) {
				this.socket = socket;
				this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.printWriter = new PrintWriter(socket.getOutputStream(), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (this.bufferedReader != null) { this.closeBuffer(); }
			if (this.printWriter != null) { this.closeWriter(); }
			if (this.socket != null) { this.closeSocket(); }
		}
	}
	
	public void setBufferedReader(BufferedReader bf) { this.bufferedReader = bf; }
	public void setPrintWriter(PrintWriter pw) { this.printWriter = pw; }
	
	// --- CREADOR SOCKET --
	public Socket createSocket(String host, int port) {
		Socket sock = null;
		try {
			sock = new Socket(host, port);
		} catch (Exception e) {
			e.printStackTrace();
			sock = null;
		}
		return sock;
	}
	
	// --- FUNCIONES GENERALES DE BUFFER READER Y WRITER ---
	public String getStrOfBuffer() {
		String line = "";
		String txt = "";
		
		try {
			if (this.bufferedReader != null) {
				while((line = this.bufferedReader.readLine()) != null) {
			         txt += line + "\n";
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			txt = "";
		}
		
		return txt;
	}
	
	public void write(String str) {
		if (this.printWriter != null) {
			this.printWriter.println(str);
		}
	}
	
	public void closeBuffer() {
		try {
			if (this.bufferedReader != null) {
				this.bufferedReader.close();
				this.bufferedReader = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeWriter() {
		if (this.printWriter != null) {
			this.printWriter.close();
			this.printWriter = null;
		}
	}
	
	public void flushWriter() {
		if (this.printWriter != null) {
			this.printWriter.flush();
		}
	}
	
	public void closeSocket() {
		try {
			if (this.socket != null) {
				this.socket.close();
				this.socket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
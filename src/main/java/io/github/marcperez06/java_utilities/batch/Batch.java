package io.github.marcperez06.java_utilities.batch;

import java.io.File;
import java.util.Arrays;

import io.github.marcperez06.java_utilities.logger.Logger;

public class Batch {

	private static final String WINDOWS_SHELL = "cmd.exe";
	private static final String C_PARAMETER = "/c";
	private static final String VISUAL_MODE = "start";
	private static final String WAIT_MODE = "/wait";
	private static final int MAX_TIME = 500;
	
	private Runtime currentApplication;
	private Process currentProcess;
	private boolean waitMode;
	
	public Batch() {
		this.currentApplication = null;
		this.currentProcess = null;
		this.waitMode = false;
	}

	public Runtime getCurrentApplication() {
		return this.currentApplication;
	}
	
	public Process getCurrentProcess() {
		return this.currentProcess;
	}

	public void executeBat(String pathOfBat, String...arguments) {
		this.executeBat(pathOfBat, false, arguments);
	}
	
	public void executeBat(String pathOfBat, boolean visualMode, String...arguments) { 
		try {
			
			File bat = new File(pathOfBat);
			
			String batArguments = (arguments != null) ? " " + this.clearBatArguments(arguments) : "";
			
			if (bat.exists()) {
				
				System.out.println("BAT EXISTS");
				
				this.waitMode = true;
				String command = pathOfBat + batArguments;
				this.executeWindowsCommandInCmd(command, visualMode);
			}

	    } catch (Exception e) { 
	    	e.printStackTrace(); 
	    }

	}
	
	public String clearBatArguments(String...arguments) {
		String clearedArguments = Arrays.toString(arguments).replaceAll("\\[", "");
		clearedArguments = clearedArguments.replaceAll("\\]", "");
		clearedArguments = clearedArguments.replaceAll(", ", "");
		return clearedArguments;
	}
	
	public void executeWindowsCommandInCmd(String command) {
		this.executeWindowsCommandInCmd(command, false);
	}
	
	public void executeWindowsCommandInCmd(String command, boolean visualMode) {
		this.currentApplication = Runtime.getRuntime(); 

        String commandToExecute = this.constructCommandToExecuteInCmd(command, visualMode);
        Logger.println("Command to execute --> " + commandToExecute);
        
        try {
        	Logger.println("Starting cmd command");
        	this.currentProcess = Runtime.getRuntime().exec(commandToExecute);
        	Logger.println("Finishing cmd command");
        	
        } catch(Exception e) {
        	e.printStackTrace();
        }

	}
	
	private String constructCommandToExecuteInCmd(String command, boolean visualMode) {
		String commandToExecute = "";
		if (visualMode == true) {
			commandToExecute = WINDOWS_SHELL + " " + C_PARAMETER;
	        commandToExecute += (visualMode) ? " " + VISUAL_MODE : "";
	        commandToExecute += (this.waitMode) ? " " + WAIT_MODE : "";
		}
        commandToExecute += (visualMode) ? " " + command : command;
        return commandToExecute;
	}
	
	public void executeCommand(String command) {
		this.currentApplication = Runtime.getRuntime(); 
		Logger.println("Command to execute --> " + command);

        try {
        	Logger.println("Starting command");
        	this.currentProcess = Runtime.getRuntime().exec(command);
        	Logger.println("Finishing command");
        	
        } catch(Exception e) {
        	e.printStackTrace();
        }

	}
	
	public void waitUntilCurrentProcessIsFinished() {
		this.waitUntilCurrentProcessIsFinished(MAX_TIME);
	}
	
	public void waitUntilCurrentProcessIsFinished(int maxTime) {
		int count = 0;
		while(this.currentProcessIsNotFinished() && count < maxTime) {
			count++;
		}
	}
	
	public int waitFor() {
		int result = -1;
		try {
			
			Logger.println("------ wait for");
			
			if (this.currentProcess != null) {
				result = this.currentProcess.waitFor();
				
				Logger.println("------ wait for ....");
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.waitMode = false;
		return result;
	}
	
	public boolean currentProcessIsNotFinished() {
		return !this.currentProcessIsFinished();
	}
	
	public boolean currentProcessIsFinished() {
		boolean isFinished = false;
		boolean isAlive = this.currentProcess.isAlive();
		boolean haveExitValue = false;
		try {
			int exitValue = this.currentProcess.exitValue();
			
			Logger.println("Exit Value: " + exitValue);
			
			haveExitValue = (exitValue > Integer.MIN_VALUE && exitValue < Integer.MAX_VALUE);
			
			isFinished = !isAlive && haveExitValue;
			
		} catch (Exception e) {
			haveExitValue = false;
		}
		
		return isFinished;
	}
	
}

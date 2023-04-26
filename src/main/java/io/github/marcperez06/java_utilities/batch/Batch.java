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
		String command = this.createCommandForFile(pathOfBat, arguments);
		this.executeCommandInCmd(command, visualMode);
	}
	
	public void executePowershellScript(String pathOfScript, boolean visualMode, String...arguments) {
		String command = this.createCommandForFile(pathOfScript, arguments);
		this.executeCommandInPowerShell(command, visualMode);
	}
	
	private String createCommandForFile(String pathOfile, String...arguments) {
		String command = null;
		
		try {
			
			File script = new File(pathOfile);
			
			String scriptArguments = (arguments != null) ? " " + this.clearArguments(arguments) : "";
			
			if (script.exists()) {
				this.waitMode = true;
				command = pathOfile + scriptArguments;
			}

	    } catch (Exception e) { 
	    	e.printStackTrace(); 
	    }
		
		return command;
	}
	
	private String clearArguments(String...arguments) {
		String clearedArguments = Arrays.toString(arguments).replaceAll("\\[", "");
		clearedArguments = clearedArguments.replaceAll("\\]", "");
		clearedArguments = clearedArguments.replaceAll(", ", "");
		return clearedArguments;
	}
	
	public void executeCommandInCmd(String command) {
		this.executeCommandInCmd(command, false);
	}
	
	public void executeCommandInCmd(String command, boolean visualMode) {
        String commandToExecute = this.constructCommandToExecuteInCmd(command, visualMode);
        this.executeCommand(commandToExecute);
	}
	
	public void executeCommandInPowerShell(String command) {
		this.executeCommandInPowerShell(command, false);
	}
	
	public void executeCommandInPowerShell(String command, boolean visualMode) {
        String commandToExecute = this.constructCommandToExecuteInPowershell(command, visualMode);
        this.executeCommand(commandToExecute);
	}
	
	private String constructCommandToExecuteInCmd(String command, boolean visualMode) {
		String commandToExecute = "";
		String cmdCommand = "";
		if (visualMode == true) {
			cmdCommand = WINDOWS_SHELL + " " + C_PARAMETER;
			cmdCommand += (visualMode) ? " " + VISUAL_MODE : "";
			cmdCommand += (this.waitMode) ? " " + WAIT_MODE : "";
		}
		
		if (command != null && !command.isEmpty()) {
			commandToExecute = (visualMode) ? cmdCommand + " " + command : command;
		}

        return commandToExecute;
	}
	
	private String constructCommandToExecuteInPowershell(String command, boolean visualMode) {
		String commandToExecute = "";
		String powerShellCommand = WINDOWS_SHELL + " " + C_PARAMETER;
		powerShellCommand += " powershell -ExecutionPolicy RemoteSigned -noprofile";
		
		if (visualMode == true) {
			powerShellCommand += (visualMode) ? " -noninteractive" : "";
		
		}
		
		if (command != null && !command.isEmpty()) {
			commandToExecute = powerShellCommand + " " + command;
		}
        
        return commandToExecute;
	}
	
	public void executeCommand(String command) {
		this.currentApplication = Runtime.getRuntime();
		
		if (command != null && !command.isEmpty()) {
			Logger.log("Command to execute --> " + command);

	        try {
	        	Logger.log("Starting command");
	        	this.currentProcess = this.currentApplication.exec(command);
	        	Logger.log("Finishing command");
	        	
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
		} else {
			Logger.log("The command is empty or null");
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

			if (this.currentProcess != null) {
				result = this.currentProcess.waitFor();
				
				Logger.log("------ wait for ....");
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
			
			Logger.log("Exit Value: " + exitValue);
			
			haveExitValue = (exitValue > Integer.MIN_VALUE && exitValue < Integer.MAX_VALUE);
			
			isFinished = !isAlive && haveExitValue;
			
		} catch (Exception e) {
			haveExitValue = false;
		}
		
		return isFinished;
	}
	
}
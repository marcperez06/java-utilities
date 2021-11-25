package io.github.marcperez06.java_utilities.robot;

import java.awt.Robot;

import io.github.marcperez06.java_utilities.logger.Logger;

import java.awt.AWTException;

public class RobotUtils {
	
	private static Robot robot;

	private RobotUtils() {
		
	}
	
	private static Robot getInstance() {
		if (robot == null) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				robot = null;
			}
		}
		return robot;
	}
	
	private static boolean canExecuteRobot() {
		boolean canExecuteRobot = (getInstance() != null);
		if (!canExecuteRobot) {
			Logger.error("Can not execute Robot Utils");
		}
		return canExecuteRobot;
	}
	
	public static boolean moveMouse(int cordX, int cordY) {
		boolean moveMouse = false;
		if (canExecuteRobot()) {
			robot.mouseMove(cordX, cordY);
			moveMouse = true;
		}
		return moveMouse;
	}

}

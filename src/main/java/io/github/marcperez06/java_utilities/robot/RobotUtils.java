package io.github.marcperez06.java_utilities.robot;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import io.github.marcperez06.java_utilities.logger.Logger;

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
	
	// ********** MOUSE CONTROL *******************
	
	/**
	 * Move mouse to coord x and y
	 * @param coordX - int
	 * @param coordY - int
	 * @return boolean - true if move mouse, false otherwise
	 */
	public static boolean moveMouse(int coordX, int coordY) {
		boolean moveMouse = false;
		if (canExecuteRobot()) {
			robot.mouseMove(coordX, coordY);
			moveMouse = true;
		}
		return moveMouse;
	}
	
	private static boolean click(int event) {
		boolean click = false;
		if (canExecuteRobot()) {
			//click con el botón derecho
	        robot.mousePress(event);
	        robot.mouseRelease(event);
	        click = true;
		}
		return click;
	}
	
	/**
	 * Click the left mouse button
	 * @return boolean - true if click left mouse button, false otherwise
	 */
	public static boolean leftClick() {
		return click(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	/**
	 * Click the right mouse button
	 * @return boolean - true if click right mouse button, false otherwise
	 */
	public static boolean rightClick() {
		return click(InputEvent.BUTTON3_DOWN_MASK);
	}
	
	/**
	 * Move wheel of mouse, positive value indicates down movement, negative value indicates up movement
	 * @param numRotations - int
	 * @return boolean - true if move mouse wheel, false otherwise
	 */
	public static boolean moveMouseWheel(int numRotations) {
		boolean moveWheel = false;
		if (canExecuteRobot()) {
			robot.mouseWheel(numRotations);
			moveWheel = true;
		}
		return moveWheel;
	}
	
	// ********** KEYBOARD CONTROL *******************

	/**
	 * Send the key specified by param
	 * @param keyCode - int (Use KeyEvent enum)
	 * @return boolean - true if send the key, false otherwise
	 */
	public static boolean sendKey(int keyCode) {
		boolean sendKey = false;
		if (canExecuteRobot()) {
			//abrir el menú inicio en windows
	        robot.keyPress(keyCode);
	        robot.keyRelease(keyCode);
	        robot.delay(250);
	        sendKey = true;
		}
		return sendKey;
	}
	
	/**
	 * Send the keys specified by param
	 * @param keyCodes - List&lt;Integer&gt; (Use KeyEvent enum)
	 * @return boolean - true if send the keys, false otherwise
	 */
	public static boolean sendKeys(List<Integer> keyCodes) {
		boolean sendKeys = false;
		boolean canExecuteRobot = canExecuteRobot();
		canExecuteRobot &= (keyCodes != null && !keyCodes.isEmpty());
		if (canExecuteRobot) {
			boolean nonStop = true;
			for (int i = 0; i < keyCodes.size() && nonStop; i++) {
				nonStop = sendKey(keyCodes.get(i));
			}
			sendKeys = nonStop;
		}
		return sendKeys;
	}
	
	/**
	 * Send the keys specified by param
	 * @param keyCodes - int[] (Use KeyEvent enum)
	 * @return boolean - true if send the keys, false otherwise
	 */
	public static boolean sendKeys(int[] keyCodes) {
		boolean sendKeys = false;
		boolean canExecuteRobot = canExecuteRobot();
		canExecuteRobot &= (keyCodes != null && keyCodes.length > 0);
		if (canExecuteRobot) {
			boolean nonStop = true;
			for (int i = 0; i < keyCodes.length && nonStop; i++) {
				nonStop = sendKey(keyCodes[i]);
			}
			sendKeys = nonStop;
		}
		return sendKeys;
	}
	
	// ************ SCREENSHOT ***********************
	
	/**
	 * Take a screenshot an save it in the specified path
	 * @param savePath - String
	 * @return boolean - true if take screenshot, false otherwise
	 */
	public boolean takeScreenshot(String savePath) {
		boolean takeScreenshot = false;
		
		if (!savePath.endsWith(".jpg")) {
			savePath = savePath + ".jpg";
		}
		
		if (canExecuteRobot()) {
			Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	        BufferedImage screenImage = robot.createScreenCapture(rectangle);
	        File file = new File(savePath);
			
	        try {
				takeScreenshot = ImageIO.write(screenImage, "jpg", file);
			} catch (IOException e) {
				takeScreenshot = false;
			};
		}
		
		return takeScreenshot;
	}
	

}
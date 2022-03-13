package tech.jaboc.jcom;

import javafx.geometry.Point2D;
import javafx.scene.input.*;

import java.util.Arrays;

public final class Input {
	static Game parent;
	
	static boolean[] currentKeys = new boolean[1024];
	static boolean[] previousKeys = new boolean[1024];
	
	static boolean[] currentMouseButtons = new boolean[16];
	static boolean[] previousMouseButtons = new boolean[16];
	
	public static int mouseWheel = 0;
	public static boolean mouseWheelChangedThisFrame = false;
	
	static Point2D mousePos = new Point2D(0, 0);
	
	//region Private functions (MUST only be called by the Game Manager)
	
	/**
	 * Initializes the input system. This should never be called by client code.
	 *
	 * @param parent The parent Game. Used to register input listeners.
	 */
	static void initialize(Game parent) {
		Input.parent = parent;
		
		reset();
	}
	
	static void mouseMovedEvent(MouseEvent e) {
		mousePos = new Point2D(e.getX(), e.getY());
	}
	
	static void mousePressedEvent(MouseEvent e) {
		mouseButtonPressed(e.getButton().ordinal());
	}
	
	static void mouseReleasedEvent(MouseEvent e) {
		mouseButtonReleased(e.getButton().ordinal());
	}
	
	static void keyPressedEvent(KeyEvent e) {
		keyPressed(e.getCode().getCode());
	}
	
	static void keyReleasedEvent(KeyEvent e) {
		keyReleased(e.getCode().getCode());
	}
	
	/**
	 * Updates the input system for a new frame. This should never be called by client code.
	 */
	static void updateInput() {
		System.arraycopy(currentKeys, 0, previousKeys, 0, 1024);
		System.arraycopy(currentMouseButtons, 0, previousMouseButtons, 0, 3);
		mouseWheelChangedThisFrame = false;
		mouseWheel = 0;
	}
	
	/**
	 * Resets the input system, releasing all keys and mouse buttons. This can be called by client code, but shouldn't if you don't know what you're doing.
	 */
	public static void reset() {
		Arrays.fill(currentKeys, false);
		Arrays.fill(previousKeys, false);
		Arrays.fill(currentMouseButtons, false);
		Arrays.fill(previousMouseButtons, false);
	}
	
	/**
	 * Sets a key to being pressed down. This should never be called by client code.
	 *
	 * @param keycode The keycode
	 */
	static void keyPressed(int keycode) {
		currentKeys[keycode] = true;
	}
	
	/**
	 * Sets a key to not being pressed down. This should never be called by client code.
	 *
	 * @param keycode The keycode
	 */
	static void keyReleased(int keycode) {
		currentKeys[keycode] = false;
	}
	
	/**
	 * Sets a mouse button to being pressed down. This should never be called by client code.
	 *
	 * @param button The mouse button ID
	 */
	static void mouseButtonPressed(int button) {
		currentMouseButtons[button] = true;
	}
	
	/**
	 * Sets a mouse button to not being pressed down. This should never be called by client code.
	 *
	 * @param button The mouse button ID
	 */
	static void mouseButtonReleased(int button) {
		currentMouseButtons[button] = false;
	}
	
	//TODO: Make mouse wheel work
	
	/**
	 * Sets the mouse wheel to being spun this frame. This should never be called by client code.
	 *
	 * @param mouseWheelValue The delta value of the mouse wheel
	 */
	static void mouseWheelSpun(int mouseWheelValue) {
		mouseWheelChangedThisFrame = true;
		mouseWheel = mouseWheelValue;
	}
	
	//endregion
	
	/**
	 * Checks if a key is currently being pressed
	 *
	 * @param keycode The keycode to check for
	 * @return True if the key is being pressed, false if it isn't
	 */
	public static boolean getKey(int keycode) {
		return currentKeys[keycode];
	}
	
	/**
	 * Checks if a key was pressed this frame
	 *
	 * @param keycode The keycode to check for
	 * @return True if the key was pressed, false if it isn't
	 */
	public static boolean getKeyDown(int keycode) {
		return currentKeys[keycode] && !previousKeys[keycode];
	}
	
	/**
	 * Checks if a key was released this frame
	 *
	 * @param keycode The keycode to check for
	 * @return True if the key was released, false if it isn't
	 */
	public static boolean getKeyUp(int keycode) {
		return !currentKeys[keycode] && previousKeys[keycode];
	}
	
	/**
	 * Checks if a mouse button is currently being pressed
	 *
	 * @param button The mouse button to check for
	 * @return True if the mouse button is being pressed, false if it isn't
	 */
	public static boolean getMouseButton(int button) {
		return currentMouseButtons[button];
	}
	
	/**
	 * Checks if a mouse button was pressed this frame
	 *
	 * @param button The mouse button to check for
	 * @return True if the mouse button was pressed, false if it isn't
	 */
	public static boolean getMouseButtonDown(int button) {
		return currentMouseButtons[button] && !previousMouseButtons[button];
	}
	
	/**
	 * Checks if a mouse button was released this frame
	 *
	 * @param button The mouse button to check for
	 * @return True if the mouse button was released, false if it isn't
	 */
	public static boolean getMouseButtonUp(int button) {
		return !currentMouseButtons[button] && previousMouseButtons[button];
	}
	
	/**
	 * Get the mouse position
	 *
	 * @return The point where the mouse is, in screen coordinates
	 */
	public static Point2D getMousePos() {
		return mousePos;
	}
	
	// Don't
	private Input() {
	
	}
}

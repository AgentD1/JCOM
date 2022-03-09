package tech.jaboc.jcom;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.transform.*;
import tech.jaboc.jcom.mission.ai.AiTeam;
import tech.jaboc.jcom.mission.common.MissionManagerProxy;
import tech.jaboc.jcom.mission.manager.*;
import tech.jaboc.jcom.mission.player.MissionRenderer;

import java.util.*;

public class Game {
	long lastNanos = 0;
	
	public JCOMApplication parent;
	
	MissionRenderer renderer;
	
	public Game(JCOMApplication parent) {
		this.parent = parent;
		
		registerInputHandles();
	}
	
	public void start() {
		lastNanos = System.nanoTime();
		
		Input.initialize(this);
		
		MissionManager missionManager = new MissionManager(2);
		
		//PlayerTeam playerTeam = new PlayerTeam(new MissionManagerProxy(missionManager));
		AiTeam aiTeam = new AiTeam(new MissionManagerProxy(missionManager));
		
		renderer = new MissionRenderer(new MissionManagerProxy(missionManager));
		
		Thread missionManagerThread = new Thread(missionManager::start);
		missionManagerThread.setDaemon(true);
		missionManagerThread.start();
		
		renderer.start();
		//new Thread(playerTeam::gameLoop).start();
		
		Thread aiTeamThread = new Thread(aiTeam::gameLoop);
		aiTeamThread.setDaemon(true);
		aiTeamThread.start();
	}
	
	GraphicsContext currentGraphics;
	
	double cx = 0.0, cy = 0.0, scale = 1.0;
	
	public void draw(GraphicsContext g, double width, double height) {
		double deltaTime = (System.nanoTime() - lastNanos) / 1_000_000_000.0;
		lastNanos = System.nanoTime();
		currentGraphics = g;
		refreshInput();
		resetTransformStack();
		
		renderer.draw(g, width, height);

//		if (Input.getKey(KeyCode.W.getCode())) {
//			cy++;
//		}
//		if (Input.getKey(KeyCode.S.getCode())) {
//			cy--;
//		}
//		if (Input.getKey(KeyCode.A.getCode())) {
//			cx++;
//		}
//		if (Input.getKey(KeyCode.D.getCode())) {
//			cx--;
//		}
//
//		if (Input.getKeyDown(KeyCode.Q.getCode())) {
//			scale /= 1.25;
//		}
//		if (Input.getKeyDown(KeyCode.E.getCode())) {
//			scale *= 1.25;
//		}
//
//		g.clearRect(0, 0, width, height);
//
//		scaleView(scale, scale, width / 2, height / 2);
//		translateView(cx, cy);
//		g.setTransform(getCurrentTransform());
//
//		g.setStroke(Color.RED);
//		g.strokeLine(0, 0, width, height);
//		g.strokeLine(0, height, width, 0);
//
//		g.setTextBaseline(VPos.TOP);
//		g.fillText(String.valueOf(deltaTime), 0, 0);
//
//
//		Point2D mousePosInWorld = screenPointToWorld(Input.getMousePos());
//		g.fillOval(mousePosInWorld.getX(), mousePosInWorld.getY(), 2, 2);
		
		Input.updateInput();
	}
	
	//region Input
	
	Queue<KeyEvent> keyPressedEvents = new ArrayDeque<>();
	Queue<KeyEvent> keyReleasedEvents = new ArrayDeque<>();
	Queue<MouseEvent> mousePressedEvents = new ArrayDeque<>();
	Queue<MouseEvent> mouseReleasedEvents = new ArrayDeque<>();
	Queue<MouseEvent> mouseMovedEvents = new ArrayDeque<>();
	
	void registerInputHandles() {
		parent.gameScene.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> mousePressedEvents.add(mouseEvent));
		parent.gameScene.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> mouseReleasedEvents.add(mouseEvent));
		parent.gameScene.addEventHandler(MouseEvent.MOUSE_MOVED, mouseEvent -> mouseMovedEvents.add(mouseEvent));
		parent.gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> keyPressedEvents.add(keyEvent));
		parent.gameScene.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> keyReleasedEvents.add(keyEvent));
		
	}
	
	void refreshInput() {
		while (!mouseMovedEvents.isEmpty()) {
			Input.mouseMovedEvent(mouseMovedEvents.remove());
		}
		while (!mousePressedEvents.isEmpty()) {
			Input.mousePressedEvent(mousePressedEvents.remove());
		}
		while (!mouseReleasedEvents.isEmpty()) {
			Input.mouseReleasedEvent(mouseReleasedEvents.remove());
		}
		while (!keyPressedEvents.isEmpty()) {
			Input.keyPressedEvent(keyPressedEvents.remove());
		}
		while (!keyReleasedEvents.isEmpty()) {
			Input.keyReleasedEvent(keyReleasedEvents.remove());
		}
	}
	
	//endregion Input
	
	//region Transform Stack
	
	Stack<Affine> transformStack = new Stack<>();
	
	void resetTransformStack() {
		transformStack.clear();
		transformStack.add(new Affine());
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void translateView(double dx, double dy) {
		transformStack.peek().appendTranslation(dx, dy);
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void rotateView(double theta) {
		transformStack.peek().appendRotation(theta);
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void rotateView(double theta, double px, double py) {
		transformStack.peek().appendRotation(theta, px, py);
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void scaleView(double s) {
		transformStack.peek().appendScale(s, s);
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void scaleView(double sx, double sy) {
		transformStack.peek().appendScale(sx, sy);
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void scaleView(double sx, double sy, double px, double py) {
		transformStack.peek().appendScale(sx, sy, px, py);
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void pushTransform() {
		transformStack.push(transformStack.peek().clone());
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void pushTransform(Affine affine) {
		transformStack.push(affine);
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public void popTransform() {
		transformStack.pop();
		if (currentGraphics != null) currentGraphics.setTransform(transformStack.peek());
	}
	
	public Affine getCurrentTransform() {
		return transformStack.peek();
	}
	
	public Point2D worldPointToScreen(Point2D worldPoint) {
		return transformStack.peek().transform(worldPoint);
	}
	
	public Point2D screenPointToWorld(Point2D screenPoint) {
		try {
			return transformStack.peek().inverseTransform(screenPoint);
		} catch (NonInvertibleTransformException e) {
			e.printStackTrace();
			return Point2D.ZERO;
		}
	}
	
	//endregion
}

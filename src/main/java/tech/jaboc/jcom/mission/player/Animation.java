package tech.jaboc.jcom.mission.player;

import javafx.scene.canvas.GraphicsContext;

public abstract class Animation {
	/**
	 * Draw
	 *
	 * @param g Graphics context to draw
	 * @return Whether the animation is over
	 */
	public abstract boolean draw(GraphicsContext g, double deltaTime);
}

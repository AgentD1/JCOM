package tech.jaboc.jcom.mission.player;

import javafx.scene.canvas.GraphicsContext;

import java.util.Iterator;

public class SequentialAnimation extends Animation {
	Iterator<Animation> animations;
	Animation currentAnimation;
	
	public SequentialAnimation(Iterable<Animation> animations) {
		this.animations = animations.iterator();
		currentAnimation = this.animations.next();
	}
	
	@Override
	public boolean draw(GraphicsContext g, double deltaTime) {
		if (currentAnimation.draw(g, deltaTime)) {
			if (!animations.hasNext()) return true;
			currentAnimation = animations.next();
		}
		return false;
	}
}

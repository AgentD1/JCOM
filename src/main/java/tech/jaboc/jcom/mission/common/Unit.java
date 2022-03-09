package tech.jaboc.jcom.mission.common;

import java.io.Serializable;

public class Unit implements Serializable {
	public int allegiance;
	public int x, y;
	
	public Unit(int allegiance, int x, int y) {
		this.allegiance = allegiance;
		this.x = x;
		this.y = y;
	}
}

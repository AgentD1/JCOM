package tech.jaboc.jcom.mission.common;

import java.io.Serializable;
import java.util.*;

public class Map implements Serializable {
	int length, width;
	public Tile[][] tiles; // TODO: This should not be public
	public List<Unit> units; // This too
	
	public Map(int length, int width) {
		this.length = length;
		this.width = width;
		units = new ArrayList<>();
	}
	
	public int getLength() {
		return length;
	}
	
	public int getWidth() {
		return width;
	}
}

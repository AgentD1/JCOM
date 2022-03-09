package tech.jaboc.jcom.mission.common;

import javafx.scene.paint.Color;

import java.io.*;

public class Tile implements Externalizable {
	public Color color;
	public boolean completed;
	
	public Wall northWall, southWall, eastWall, westWall;
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(completed);
		out.writeDouble(color.getRed());
		out.writeDouble(color.getGreen());
		out.writeDouble(color.getBlue());
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		completed = in.readBoolean();
		color = new Color(in.readDouble(), in.readDouble(), in.readDouble(), 1.0);
	}
}

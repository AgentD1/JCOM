package tech.jaboc.jcom.mission.common;

import javafx.scene.paint.Color;

import java.io.*;

public class Tile implements Externalizable {
	public Color color;
	public boolean completed;
	
	public Wall northWall, southWall, eastWall, westWall;
	
	public boolean passable;
	
	public int x, y;
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(completed);
		out.writeDouble(color.getRed());
		out.writeDouble(color.getGreen());
		out.writeDouble(color.getBlue());
		out.writeInt(x);
		out.writeInt(y);
		out.writeBoolean(passable);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		completed = in.readBoolean();
		color = new Color(in.readDouble(), in.readDouble(), in.readDouble(), 1.0);
		x = in.readInt();
		y = in.readInt();
		passable = in.readBoolean();
	}
}

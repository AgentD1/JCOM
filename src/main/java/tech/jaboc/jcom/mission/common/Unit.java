package tech.jaboc.jcom.mission.common;

import java.io.Serializable;

public class Unit implements Serializable {
	public int allegiance;
	public int x, y;
	
	public int numActionsLeft = 2;
	public final int numActions;
	
	public final int id;
	
	public Unit(int allegiance, int x, int y, int numActions, int unitId) {
		this.allegiance = allegiance;
		this.x = x;
		this.y = y;
		
		this.numActions = numActions;
		numActionsLeft = numActions;
		
		id = unitId;
	}
}

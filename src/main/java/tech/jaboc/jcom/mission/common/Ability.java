package tech.jaboc.jcom.mission.common;

import java.io.Serializable;

public abstract class Ability implements Serializable {
	public final int unitId, abilityId;
	
	public Ability(int unitId, int abilityId) {
		this.unitId = unitId;
		this.abilityId = abilityId;
	}
	
	public abstract boolean run(Map map);
}

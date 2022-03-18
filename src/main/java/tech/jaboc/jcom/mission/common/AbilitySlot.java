package tech.jaboc.jcom.mission.common;

import java.io.Serializable;

public class AbilitySlot implements Serializable {
	public int cooldown;
	public int maxCooldown;
	
	public Unit.ActionType actionType;
	
	public AbilitySlot(Unit.ActionType actionType) {
		cooldown = 0;
		maxCooldown = 0;
		this.actionType = actionType;
	}
	
	public AbilitySlot(int cooldown, int maxCooldown, Unit.ActionType actionType) {
		this.cooldown = cooldown;
		this.maxCooldown = maxCooldown;
		this.actionType = actionType;
	}
	
	public boolean canUseAbilitySlot(Unit unit) {
		if (cooldown > 0) return false;
		return unit.canUseAbility(actionType);
	}
	
	public void useAbility(Unit unit) {
		unit.useAbility(actionType);
		cooldown = maxCooldown;
	}
	
	public void nextTurn() {
		if (cooldown > 0) {
			cooldown--;
		}
	}
}

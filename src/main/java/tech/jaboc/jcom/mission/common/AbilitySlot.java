package tech.jaboc.jcom.mission.common;

public class AbilitySlot {
	public int cooldown;
	public int maxCooldown;
	
	public AbilitySlot() {
		cooldown = 0;
		maxCooldown = 0;
	}
	
	public AbilitySlot(int cooldown, int maxCooldown) {
		this.cooldown = cooldown;
		this.maxCooldown = maxCooldown;
	}
}

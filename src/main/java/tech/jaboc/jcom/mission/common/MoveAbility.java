package tech.jaboc.jcom.mission.common;

import tech.jaboc.jcom.mission.ai.AiTeam;
import tech.jaboc.jcom.mission.manager.MissionManager;
import tech.jaboc.jcom.mission.message.UseAbilityAction;
import tech.jaboc.jcom.mission.player.PlayerTeam;

import java.io.Serializable;
import java.util.*;

public class MoveAbility extends Ability implements IAllTeamsExecutable {
	public MoveAbility(int unitId, int abilityId, int destX, int destY, Collection<Step> steps) {
		super(unitId, abilityId);
		
		this.destX = destX;
		this.destY = destY;
		
		this.steps = new ArrayList<>(steps);
	}
	
	public List<Step> steps;
	
	public int destX, destY;
	
	@Override
	public boolean run(Map map) {
		Unit myUnit = map.getUnitFromId(unitId);
		if (myUnit == null) throw new DesyncException("No unit found with the id " + unitId + "!");
		myUnit.x = destX;
		myUnit.y = destY;
		return true;
	}
	
	@Override
	public void executeMissionManagerAction(MissionManager missionManager) {
		if (run(missionManager.map)) {
			for (Team team : missionManager.getTeams()) {
				team.proxy.receiveMessage(new UseAbilityAction(this));
			}
		}
	}
	
	@Override
	public void executePlayerTeamAction(PlayerTeam team) {
		Unit unit = team.localMapCopy.getUnitFromId(unitId);
		team.renderer.playUnitMovementAnimation(unit, this);
		run(team.localMapCopy);
	}
	
	@Override
	public void executeAiTeamMessage(AiTeam team) {
		run(team.map);
	}
	
	public static class Step implements Serializable {
		public int x, y;
		
		public Step(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return "Step{" +
					"x=" + x +
					", y=" + y +
					'}';
		}
	}
}

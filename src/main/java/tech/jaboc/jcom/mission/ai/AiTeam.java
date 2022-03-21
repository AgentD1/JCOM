package tech.jaboc.jcom.mission.ai;

import tech.jaboc.jcom.mission.common.Map;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

import java.util.*;

public class AiTeam {
	public MissionManagerProxy missionManagerProxy;
	
	public AiTeam(MissionManagerProxy missionManagerProxy, String teamName) {
		this.missionManagerProxy = missionManagerProxy;
		this.teamName = teamName;
	}
	
	public Map map;
	public List<Team> teams;
	
	public String teamName;
	public int teamId = -1;
	
	Random random = new Random();
	
	public void gameLoop() {
		missionManagerProxy.sendMessage(new TeamJoinRequest(new Team(teamName, missionManagerProxy)));
		
		while (true) {
			Message message = missionManagerProxy.getNextMessage();
			if (message instanceof IAiTeamExecutable atMessage) {
				atMessage.executeAiTeamMessage(this);
			}
		}
	}
	
	public void executeTurn() {
		for (Unit unit : map.units) {
			if (unit.allegiance == teamId) {
				while (true) {
					int destX = random.nextInt(map.getLength());
					int destY = random.nextInt(map.getWidth());
					if (map.units.stream().anyMatch(u -> u.x == destX && u.y == destY)) {
						continue;
					}
					//missionManagerProxy.sendMessage(new UseAbilityAction(new MoveAbility(unit.id, 0, destX, destY)));
					break;
				} // TODO: make the AI team obey the new moveAbility laws
				// TODO: Also make the units have movement distances, then make them use that
				// TODO: Also next turn isnt working
			}
		}
		
		missionManagerProxy.sendMessage(new NextTurnAction(-1));
	}
}

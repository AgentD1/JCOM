package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.ai.AiTeam;
import tech.jaboc.jcom.mission.common.IAllTeamsExecutable;
import tech.jaboc.jcom.mission.manager.MissionManager;
import tech.jaboc.jcom.mission.player.PlayerTeam;

public class NextTurnAction extends Message implements IAllTeamsExecutable {
	public int nextTeamId;
	
	public NextTurnAction(int nextTeamId) {
		this.nextTeamId = nextTeamId;
	}
	
	@Override
	public void executeAiTeamMessage(AiTeam team) {
		if (nextTeamId == team.teamId) {
			team.executeTurn();
		}
	}
	
	@Override
	public void executeMissionManagerAction(MissionManager missionManager) {
		missionManager.nextTurn();
	}
	
	@Override
	public void executePlayerTeamAction(PlayerTeam team) {
		team.isMyTurn = nextTeamId == team.teamId;
	}
}

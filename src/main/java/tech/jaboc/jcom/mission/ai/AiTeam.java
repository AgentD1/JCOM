package tech.jaboc.jcom.mission.ai;

import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

public class AiTeam {
	public MissionManagerProxy missionManagerProxy;
	
	public AiTeam(MissionManagerProxy missionManagerProxy) {
		this.missionManagerProxy = missionManagerProxy;
	}
	
	public boolean justSent = false;
	
	public Map map;
	
	public void gameLoop() {
		missionManagerProxy.sendMessage(new TeamJoinRequest(new Team("AI", missionManagerProxy)));
		
		while (true) {
			Message message = missionManagerProxy.getNextMessage();
			if (message instanceof IAiTeamExecutable atMessage) {
				atMessage.executeAiTeamMessage(this);
			}
		}
	}
}

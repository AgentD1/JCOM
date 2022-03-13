package tech.jaboc.jcom.mission.player;

import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

public class PlayerTeam {
	public MissionManagerProxy missionManagerProxy;
	public MissionRenderer renderer;
	
	public Map localMapCopy;
	
	
	public PlayerTeam(MissionManagerProxy missionManagerProxy) {
		this.missionManagerProxy = missionManagerProxy;
	}
	
	public boolean justSent = false;
	public void gameLoop() {
		missionManagerProxy.sendMessage(new TeamJoinRequest(new Team("Player", missionManagerProxy)));
		
		while (true) {
			Message message = missionManagerProxy.getNextMessage();
			System.out.println(message);
			if (message instanceof IPlayerTeamExecutable ptMessage) {
				ptMessage.executePlayerTeamAction(this);
			}
		}
	}
}

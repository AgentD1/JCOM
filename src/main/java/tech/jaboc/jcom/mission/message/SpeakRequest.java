package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.manager.MissionManager;

public class SpeakRequest extends Message implements IMissionManagerExecutable {
	public String text;
	
	public SpeakRequest(String text) {
		this.text = text;
	}
	
	@Override
	public void executeMissionManagerAction(MissionManager missionManager) {
		Message speak = new SpeakAction(text);
		for (Team team : missionManager.getTeams()) {
			team.proxy.receiveMessage(speak);
		}
	}
}

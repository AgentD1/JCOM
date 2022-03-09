package tech.jaboc.jcom.mission.player;

import tech.jaboc.jcom.mission.common.Team;
import tech.jaboc.jcom.mission.common.MissionManagerProxy;
import tech.jaboc.jcom.mission.message.*;

import java.util.Scanner;

public class PlayerTeam {
	public MissionManagerProxy missionManagerProxy;
	
	public PlayerTeam(MissionManagerProxy missionManagerProxy) {
		this.missionManagerProxy = missionManagerProxy;
	}
	
	public void gameLoop() {
		missionManagerProxy.sendMessage(new TeamJoinRequest(new Team("Player", missionManagerProxy)));
		
		boolean justSent = false;
		while (true) {
			Message message = missionManagerProxy.getNextMessage();
			if (message instanceof SpeakAction action) {
				if (justSent) {
					justSent = false;
					continue;
				}
				System.out.println("Player received " + action.message);
				System.out.print("Enter a string: ");
				SpeakRequest request = new SpeakRequest(new Scanner(System.in).nextLine());
				missionManagerProxy.sendMessage(request);
				justSent = true;
			}
		}
	}
}

package tech.jaboc.jcom.mission.ai;

import tech.jaboc.jcom.mission.common.Team;
import tech.jaboc.jcom.mission.common.MissionManagerProxy;
import tech.jaboc.jcom.mission.message.*;

import java.util.Random;

public class AiTeam {
	public MissionManagerProxy missionManagerProxy;
	
	public AiTeam(MissionManagerProxy missionManagerProxy) {
		this.missionManagerProxy = missionManagerProxy;
	}
	
	public void gameLoop() {
		missionManagerProxy.sendMessage(new TeamJoinRequest(new Team("AI", missionManagerProxy)));
		
		boolean justSent = false;
		while (true) {
			Message message = missionManagerProxy.getNextMessage();
			if (message instanceof SpeakAction action) {
				if (justSent) {
					justSent = false;
					continue;
				}
				System.out.println("Ai received " + action.message);
				
				// https://www.baeldung.com/java-random-string
				
				int leftLimit = 48; // numeral '0'
				int rightLimit = 122; // letter 'z'
				int targetStringLength = 32;
				Random random = new Random();
				
				String generatedString = random.ints(leftLimit, rightLimit + 1)
						.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
						.limit(targetStringLength)
						.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
						.toString();
				
				missionManagerProxy.sendMessage(new SpeakRequest(generatedString));
				
				justSent = true;
			}
		}
	}
}
